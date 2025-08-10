// 상수 정의
const CONSTANTS = {
  LOADING_DURATION: 3000,
  MAX_SAVED_RESULTS: 10,
  STORAGE_KEYS: {
    USER_DATA: "userFormData",
    SELECTION_DATA: "selectionData",
    FORTUNE_RESULTS: "fortuneResults"
  },
  PAGES: {
    SELECTION: "/selection.html"
  },
  MESSAGES: {
    COPY_SUCCESS: "운세 결과가 복사되었습니다! 📋",
    SHARE_SUCCESS: "공유 링크가 복사되었습니다! 🔗",
    SAVE_SUCCESS: "운세가 저장되었습니다! 나중에 정확도를 확인해보세요 🔮",
    COPY_FAILED: "복사에 실패했습니다. 😅",
    SHARE_FAILED: "공유에 실패했습니다. 😅",
    SAVE_FAILED: "저장에 실패했습니다. 😅",
    LOGIN_REQUIRED: "로그인 페이지로 이동합니다! 🔐",
    NO_USER_DATA: "사용자 정보를 불러올 수 없습니다😱.<br>다시 시도해주세요😵.",
    LOGIN_CONFIRM: "저장하려면 로그인이 필요합니다.\n로그인 페이지로 이동하시겠습니까?"
  }
};

// DOM 요소 캐싱
const elements = {
  userInfo: null,
  aiIcon: null,
  aiName: null,
  loadingScreen: null,
  resultsScreen: null,
  resultsContainer: null
};

// 데이터 매핑
const DATA_MAPS = {
  fortuneIcons: {
    overall: "🔮",
    money: "💰",
    love: "💕",
    career: "💼",
    study: "📚",
    luck: "🍀",
    family: "🏠",
    health: "💪"
  },
  
  fortuneNames: {
    overall: "종합운",
    money: "재물운",
    love: "애정운",
    career: "직장·사업운",
    study: "학업·시험운",
    luck: "행운",
    family: "가정운",
    health: "건강운"
  },
  
  aiInfo: {
    claude: { icon: "🎆", name: "Claude" },
    gemini: { icon: "🪂", name: "Gemini" },
    gpt: { icon: "🚀", name: "GPT" }
  },
  
  calendarTypes: {
    solar: "양력",
    lunar: "음력(평달)",
    lunar_leap: "음력(윤달)"
  }
};

// 더미 운세 데이터 (실제로는 API에서 받아옴)
const dummyFortuneData = {
  overall: "전반적으로 안정된 기운이 흐르는 시기입니다. 새로운 시작을 위한 준비를 하기에 좋은 때이며, 과거의 경험을 바탕으로 현명한 선택을 할 수 있을 것입니다.",
  money: "금전적으로는 꾸준한 흐름을 보이는 시기입니다. 무리한 투자보다는 안정적인 저축이나 계획적인 소비가 도움이 될 것입니다.",
  love: "사랑에 있어서는 진실한 마음이 통하는 시기입니다. 상대방을 이해하려는 노력과 소통이 관계 발전의 열쇠가 될 것입니다.",
  career: "직장이나 사업에서는 새로운 기회가 찾아올 가능성이 높습니다. 준비된 자에게 행운이 따르니, 역량 개발에 힘쓰시기 바랍니다.",
  study: "학습이나 시험 준비에는 집중력이 필요한 시기입니다. 체계적인 계획을 세우고 꾸준히 노력한다면 좋은 결과를 얻을 수 있을 것입니다.",
  luck: "행운의 기운이 서서히 다가오고 있습니다. 작은 기회들을 놓치지 말고, 긍정적인 마음가짐을 유지하는 것이 중요합니다.",
  family: "가족 관계에서는 화합과 이해가 중요한 시기입니다. 서로에 대한 배려와 관심이 가정의 평화를 가져다줄 것입니다.",
  health: "건강 관리에 특별한 주의가 필요한 시기입니다. 규칙적인 생활 패턴과 적절한 운동, 균형 잡힌 식단을 유지하시기 바랍니다."
};

// 유틸리티 함수들
const utils = {
  // 안전한 2자리 패딩 (브라우저 호환성)
  padZero: (str) => str.length === 1 ? '0' + str : str,
  
  // 안전한 sessionStorage 읽기
  getStorageData: (key) => {
    try {
      const stored = sessionStorage.getItem(key);
      return stored ? JSON.parse(stored) : null;
    } catch (e) {
      console.error(`${key} 파싱 실패:`, e);
      return null;
    }
  },
  
  // 안전한 localStorage 저장
  saveToLocalStorage: (key, data) => {
    try {
      localStorage.setItem(key, JSON.stringify(data));
      return true;
    } catch (e) {
      console.error(`${key} 저장 실패:`, e);
      return false;
    }
  },
  
  // 안전한 localStorage 읽기
  getFromLocalStorage: (key, defaultValue = []) => {
    try {
      const stored = localStorage.getItem(key);
      return stored ? JSON.parse(stored) : defaultValue;
    } catch (e) {
      console.error(`${key} 읽기 실패:`, e);
      return defaultValue;
    }
  }
};

// 클립보드 관리자
const ClipboardManager = {
  async copy(text) {
    if (navigator.clipboard && window.isSecureContext) {
      try {
        await navigator.clipboard.writeText(text);
        return true;
      } catch (e) {
        console.error("클립보드 복사 실패:", e);
        return this.fallbackCopy(text);
      }
    } else {
      return this.fallbackCopy(text);
    }
  },
  
  fallbackCopy(text) {
    try {
      const textArea = document.createElement("textarea");
      textArea.value = text;
      textArea.style.position = "fixed";
      textArea.style.opacity = "0";
      document.body.appendChild(textArea);
      textArea.select();
      const success = document.execCommand("copy");
      document.body.removeChild(textArea);
      return success;
    } catch (e) {
      console.error("Fallback 복사 실패:", e);
      return false;
    }
  }
};

// 토스트 관리자
const ToastManager = {
  show(message) {
    // 실제 구현에서는 더 나은 토스트 UI 사용
    alert(message);
  }
};

// 스크롤 애니메이션 관리자
const ScrollAnimationManager = {
  observer: null,
  
  init() {
    this.observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.style.animationPlayState = "running";
          }
        });
      },
      { threshold: 0.1 }
    );
  },
  
  observe(elements) {
    if (!this.observer) this.init();
    elements.forEach(element => this.observer.observe(element));
  },
  
  cleanup() {
    if (this.observer) {
      this.observer.disconnect();
      this.observer = null;
    }
  }
};

// DOM 요소 초기화
function initializeElements() {
  elements.userInfo = document.getElementById("user-info");
  elements.aiIcon = document.getElementById("ai-icon");
  elements.aiName = document.getElementById("ai-name");
  elements.loadingScreen = document.getElementById("loading-screen");
  elements.resultsScreen = document.getElementById("results-screen");
  elements.resultsContainer = document.getElementById("results-container");
}

// 사용자 정보 표시 업데이트
function updateUserDisplay() {
  if (!elements.userInfo) return;
  
  const userData = utils.getStorageData(CONSTANTS.STORAGE_KEYS.USER_DATA);
  
  if (!userData) {
    elements.userInfo.innerHTML = CONSTANTS.MESSAGES.NO_USER_DATA;
    return;
  }
  
  const genderText = userData.gender === "male" ? "남성" : "여성";
  const calendarText = DATA_MAPS.calendarTypes[userData.calendar] || "양력";
  const formattedMonth = utils.padZero(userData.month);
  const formattedDay = utils.padZero(userData.day);
  
  elements.userInfo.innerHTML = `${userData.city} ${genderText} ${calendarText}<br>${userData.year}년 ${formattedMonth}월 ${formattedDay}일${userData.time ? ' ' + userData.time : ''}`;
}

// AI 정보 업데이트
function updateAIInfo(ai) {
  if (!elements.aiIcon || !elements.aiName) return;
  
  const aiData = DATA_MAPS.aiInfo[ai] || DATA_MAPS.aiInfo.gpt;
  elements.aiIcon.textContent = aiData.icon;
  elements.aiName.textContent = aiData.name;
}

// 로딩 시작
function startLoading(fortunes, period) {
  console.log("startLoading 호출 - 받은 운세들:", fortunes, "주기:", period); // 디버깅용
  setTimeout(() => {
    showResults(fortunes, period);
  }, CONSTANTS.LOADING_DURATION);
}

// 결과 표시
function showResults(fortunes, period) {
  if (!elements.loadingScreen || !elements.resultsScreen || !elements.resultsContainer) return;

  console.log("showResults 호출 - 선택된 운세들:", fortunes); // 디버깅용

  // 로딩 화면 숨기기
  elements.loadingScreen.style.display = "none";

  // 결과 생성
  elements.resultsContainer.innerHTML = "";

  // 선택된 운세들만 표시
  if (Array.isArray(fortunes) && fortunes.length > 0) {
    fortunes.forEach((fortuneType) => {
      console.log("처리 중인 운세:", fortuneType, "데이터 존재:", !!dummyFortuneData[fortuneType]); // 디버깅용
      if (dummyFortuneData[fortuneType]) {
        const section = createFortuneSection(fortuneType, period);
        elements.resultsContainer.appendChild(section);
      }
    });
  } else {
    console.error("운세 배열이 비어있거나 올바르지 않습니다:", fortunes);
    // 기본값으로 종합운 표시
    const section = createFortuneSection("overall", period);
    elements.resultsContainer.appendChild(section);
  }

  console.log("최종 생성된 섹션 수:", elements.resultsContainer.children.length); // 디버깅용

  // 결과 화면 표시
  elements.resultsScreen.style.display = "flex";
  elements.resultsScreen.style.flexDirection = "column";

  // 스크롤 애니메이션 시작
  setTimeout(() => {
    const sections = document.querySelectorAll(".fortune-section");
    ScrollAnimationManager.observe(sections);
  }, 100);
}

// 운세 섹션 생성
function createFortuneSection(fortuneType, period) {
  const section = document.createElement("div");
  section.className = "fortune-section";

  section.innerHTML = `
    <div class="fortune-title">
      <span class="fortune-icon">${DATA_MAPS.fortuneIcons[fortuneType]}</span>
      <span>${DATA_MAPS.fortuneNames[fortuneType]}</span>
    </div>
    <div class="fortune-content-wrapper">
      <div class="fortune-content">
        ${dummyFortuneData[fortuneType]}
      </div>
    </div>
  `;

  return section;
}

// 공유 텍스트 생성
function generateShareText() {
  if (!elements.aiName || !elements.userInfo) return "";

  const aiName = elements.aiName.textContent;
  const userInfo = elements.userInfo.textContent;
  const sections = document.querySelectorAll(".fortune-section");

  let shareText = `🔮 ${aiName} AI 운세 결과\n\n👤 ${userInfo}\n\n`;

  sections.forEach((section) => {
    const titleElement = section.querySelector(".fortune-title span:nth-child(2)");
    const contentElement = section.querySelector(".fortune-content");

    if (titleElement && contentElement) {
      const title = titleElement.textContent;
      const content = contentElement.textContent;
      shareText += `${title}\n${content}\n\n`;
    }
  });

  shareText += "✨ LUCKY LOG에서 확인하세요!";
  return shareText;
}

// 메인 함수들
function goToSelection() {
  window.location.href = CONSTANTS.PAGES.SELECTION;
}

async function copyResults() {
  const resultsText = generateShareText();
  const success = await ClipboardManager.copy(resultsText);
  ToastManager.show(success ? CONSTANTS.MESSAGES.COPY_SUCCESS : CONSTANTS.MESSAGES.COPY_FAILED);
}

async function shareResults() {
  const shareUrl = window.location.href;
  const success = await ClipboardManager.copy(shareUrl);
  ToastManager.show(success ? CONSTANTS.MESSAGES.SHARE_SUCCESS : CONSTANTS.MESSAGES.SHARE_FAILED);
}

function saveResults() {
  const isLoggedIn = false; // 로그인 체크 로직 (추후 구현)

  if (!isLoggedIn) {
    if (confirm(CONSTANTS.MESSAGES.LOGIN_CONFIRM)) {
      ToastManager.show(CONSTANTS.MESSAGES.LOGIN_REQUIRED);
    }
    return;
  }

  const resultsData = {
    timestamp: new Date().toISOString(),
    ai: elements.aiName?.textContent || "",
    userInfo: elements.userInfo?.textContent || "",
    results: generateShareText()
  };

  const savedResults = utils.getFromLocalStorage(CONSTANTS.STORAGE_KEYS.FORTUNE_RESULTS);
  savedResults.unshift(resultsData);

  // 최대 개수 제한
  if (savedResults.length > CONSTANTS.MAX_SAVED_RESULTS) {
    savedResults.splice(CONSTANTS.MAX_SAVED_RESULTS);
  }

  const success = utils.saveToLocalStorage(CONSTANTS.STORAGE_KEYS.FORTUNE_RESULTS, savedResults);
  ToastManager.show(success ? CONSTANTS.MESSAGES.SAVE_SUCCESS : CONSTANTS.MESSAGES.SAVE_FAILED);
}

// 키보드 이벤트 핸들러
function handleKeyboardEvents(e) {
  if (e.key === "Escape") {
    goToSelection();
  } else if (e.ctrlKey && e.key === "s") {
    e.preventDefault();
    saveResults();
  } else if (e.ctrlKey && e.shiftKey && e.key === "S") {
    e.preventDefault();
    shareResults();
  }
}

// 페이지 초기화
function initializePage() {
  initializeElements();
  updateUserDisplay();

  const selectionData = utils.getStorageData(CONSTANTS.STORAGE_KEYS.SELECTION_DATA);

  if (selectionData) {
    console.log("선택된 데이터:", selectionData); // 디버깅용
    updateAIInfo(selectionData.ai);
    startLoading(selectionData.fortunes, selectionData.period);
  } else {
    console.error("선택 데이터가 없습니다. 기본값을 사용합니다.");
    updateAIInfo("gpt");
    startLoading(["overall"], "monthly");
  }
}

// 이벤트 리스너 등록
function attachEventListeners() {
  document.addEventListener("keydown", handleKeyboardEvents);
  
  // 페이지 언로드 시 정리
  window.addEventListener("beforeunload", () => {
    ScrollAnimationManager.cleanup();
  });
}

// DOM 로드 완료 후 초기화
if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", () => {
    initializePage();
    attachEventListeners();
  });
} else {
  initializePage();
  attachEventListeners();
}