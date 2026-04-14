const API_BASE_URL = "http://localhost:8080/llm-translator/api/translate";

const usernameInput = document.getElementById("username");
const passwordInput = document.getElementById("password");
const sourceTextInput = document.getElementById("sourceText");
const resultTextInput = document.getElementById("resultText");
const statusText = document.getElementById("status");

let lastSelection = "";
let autoTranslateTimeout = null;
let isTranslating = false;

function setStatus(message, isError = false) {
  statusText.textContent = message;
  statusText.style.color = isError ? "crimson" : "green";
}

async function readSelectedTextFromPage() {
  const [tab] = await chrome.tabs.query({
    active: true,
    currentWindow: true
  });

  if (!tab?.id) {
    throw new Error("No active tab found.");
  }

  const results = await chrome.scripting.executeScript({
    target: { tabId: tab.id },
    func: () => window.getSelection().toString().trim()
  });

  return results?.[0]?.result || "";
}

async function translateText(text) {
  const username = usernameInput.value.trim();
  const password = passwordInput.value.trim();

  if (!username || !password) {
    setStatus("Enter your WildFly username and password.", true);
    return;
  }

  if (!text) {
    return;
  }

  if (isTranslating) {
    return;
  }

  try {
    isTranslating = true;
    setStatus("Translating...");
    resultTextInput.value = "";

    const url = `${API_BASE_URL}?text=${encodeURIComponent(text)}`;

    const response = await fetch(url, {
      method: "GET",
      headers: {
        "Accept": "application/json",
        "Authorization": "Basic " + btoa(`${username}:${password}`)
      }
    });

    if (response.status === 401) {
      setStatus("Invalid WildFly username or password.", true);
      return;
    }

    if (response.status === 403) {
      setStatus("Access forbidden for this WildFly user.", true);
      return;
    }

    if (!response.ok) {
      const errorText = await response.text();
      setStatus(`Server error ${response.status}: ${errorText}`, true);
      return;
    }

    const data = await response.json();

    if (data.translation) {
      resultTextInput.value = data.translation;
    } else if (data.translatedText) {
      resultTextInput.value = data.translatedText;
    } else if (data.result) {
      resultTextInput.value = data.result;
    } else {
      resultTextInput.value = JSON.stringify(data, null, 2);
    }

    setStatus("Translation done.");
  } catch (error) {
    setStatus(error.message || "Translation failed.", true);
  } finally {
    isTranslating = false;
  }
}

async function checkSelectionAndAutoTranslate() {
  try {
    const selectedText = await readSelectedTextFromPage();

    if (!selectedText) {
      return;
    }

    if (selectedText === lastSelection) {
      return;
    }

    lastSelection = selectedText;
    sourceTextInput.value = selectedText;
    resultTextInput.value = "";
    setStatus("New text selected.");

    if (autoTranslateTimeout) {
      clearTimeout(autoTranslateTimeout);
    }

    autoTranslateTimeout = setTimeout(() => {
      translateText(selectedText);
    }, 500);
  } catch (error) {
    // stay silent
  }
}

setInterval(checkSelectionAndAutoTranslate, 800);