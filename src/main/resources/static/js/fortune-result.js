/**
 * Fortune Result Page JavaScript
 * Thymeleaf 템플릿용 최소화된 JavaScript
 */

// 상수 정의
const CONSTANTS = {
  LOADING_DURATION: 3000,
  PAGES: {
    SELECTION: "/fortune-option",
  },
  MESSAGES: {
    COPY_SUCCESS: "운세 결과가 복사되었습니다! 📋",
    SHARE_SUCCESS: "공유 링크가 복사되었습니다! 🔗",
    SAVE_SUCCESS: "운세가 저장되었습니다! 나중에 정확도를 확인해보세요 🔮",
    COPY_FAILED: "복사에 실패했습니다. 😅",
    SHARE_FAILED: "공유에 실패했습니다. 😅",
    SAVE_FAILED: "저장에 실패했습니다. 😅",
    LOGIN_REQUIRED: "로그인 페이지로 이동합니다! 🔐",
    LOGIN_CONFIRM:
      "저장하려면 로그인이 필요합니다.\n로그인 페이지로 이동하시겠습니까?",
  },
};

// DOM 요소 캐싱
const elements = {
  loadingScreen: null,
  resultsScreen: null,
  shareText: null,
};

// 클립보드 관리자
const ClipboardManager = {
  /**
   * 텍스트를 클립보드에 복사
   * @param {string} text 복사할 텍스트
   * @returns {Promise<boolean>} 성공 여부
   */
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

  /**
   * 대체 복사 방법 (구형 브라우저 지원)
   * @param {string} text 복사할 텍스트
   * @returns {boolean} 성공 여부
   */
  fallbackCopy(text) {
    try {
      const textArea = document.createElement("textarea");
      textArea.value = text;
      textArea.style.position = "fixed";
      textArea.style.opacity = "0";
      textArea.style.top = "-9999px";
      textArea.style.left = "-9999px";
      document.body.appendChild(textArea);
      textArea.select();
      textArea.setSelectionRange(0, 99999); // 모바일 지원
      const success = document.execCommand("copy");
      document.body.removeChild(textArea);
      return success;
    } catch (e) {
      console.error("Fallback 복사 실패:", e);
      return false;
    }
  },
};

// 토스트 관리자
const ToastManager = {
  /**
   * 토스트 메시지 표시
   * @param {string} message 표시할 메시지
   */
  show(message) {
    // 실제 구현에서는 더 나은 토스트 UI 사용
    // 예: toast 라이브러리, 커스텀 모달 등
    alert(message);
  },
};

// 페이지 초기화 관리자
const PageManager = {
  /**
   * DOM 요소들을 초기화
   */
  initializeElements() {
    elements.loadingScreen = document.getElementById("loading-screen");
    elements.resultsScreen = document.getElementById("results-screen");
    elements.shareText = document.getElementById("shareText");
  },

  /**
   * 로딩 화면에서 결과 화면으로 전환
   */
  showResults() {
    if (!elements.loadingScreen || !elements.resultsScreen) return;

    if (elements.loadingScreen.style.display !== "none") {
      setTimeout(() => {
        elements.loadingScreen.style.display = "none";
        elements.resultsScreen.style.display = "flex";
        elements.resultsScreen.style.flexDirection = "column";

        // 애니메이션 트리거
        this.triggerAnimations();
      }, CONSTANTS.LOADING_DURATION);
    }
  },

  /**
   * 결과 섹션 애니메이션 트리거
   */
  triggerAnimations() {
    setTimeout(() => {
      const sections = document.querySelectorAll(".fortune-section");
      sections.forEach((section) => {
        section.style.animationPlayState = "running";
      });
    }, 100);
  },
};

// 키보드 이벤트 관리자
const KeyboardManager = {
  /**
   * 키보드 이벤트 핸들러
   * @param {KeyboardEvent} e 키보드 이벤트
   */
  handleKeyboard(e) {
    if (e.key === "Escape") {
      goToSelection();
    } else if (e.ctrlKey && e.key === "s") {
      e.preventDefault();
      saveResults();
    } else if (e.ctrlKey && e.shiftKey && e.key === "S") {
      e.preventDefault();
      shareResults();
    }
  },

  /**
   * 키보드 이벤트 등록
   */
  init() {
    document.addEventListener("keydown", this.handleKeyboard);
  },

  /**
   * 키보드 이벤트 해제
   */
  destroy() {
    document.removeEventListener("keydown", this.handleKeyboard);
  },
};

// 메인 기능 함수들
/**
 * 운세 선택 페이지로 이동
 */
function goToSelection() {
  window.location.href = CONSTANTS.PAGES.SELECTION;
}

/**
 * 운세 결과를 클립보드에 복사
 */
async function copyResults() {
  if (!elements.shareText) {
    ToastManager.show(CONSTANTS.MESSAGES.COPY_FAILED);
    return;
  }

  const resultsText = elements.shareText.textContent.trim();
  const success = await ClipboardManager.copy(resultsText);
  ToastManager.show(
    success ? CONSTANTS.MESSAGES.COPY_SUCCESS : CONSTANTS.MESSAGES.COPY_FAILED
  );
}

/**
 * 현재 페이지 URL을 클립보드에 복사 (공유)
 */
async function shareResults() {
  const shareUrl = window.location.href;
  const success = await ClipboardManager.copy(shareUrl);
  ToastManager.show(
    success ? CONSTANTS.MESSAGES.SHARE_SUCCESS : CONSTANTS.MESSAGES.SHARE_FAILED
  );
}

/**
 * 운세 결과를 서버에 저장
 */
function saveResults() {
  const saveButton = document.querySelector("[data-login-required]");
  const isLoginRequired =
    saveButton && saveButton.dataset.loginRequired === "true";

  if (isLoginRequired) {
    if (confirm(CONSTANTS.MESSAGES.LOGIN_CONFIRM)) {
      ToastManager.show(CONSTANTS.MESSAGES.LOGIN_REQUIRED);
      // 실제로는 로그인 페이지로 리다이렉트
      // window.location.href = '/login';
    }
    return;
  }

  // 로그인 상태라면 서버로 저장 요청
  // 실제 구현에서는 AJAX 요청을 통해 서버에 저장
  saveToServer();
}

/**
 * 서버에 운세 결과 저장 (AJAX 요청)
 */
async function saveToServer() {
  try {
    // 실제 구현 예시
    /*
        const response = await fetch('/api/fortune/save', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            },
            body: JSON.stringify({
                results: elements.shareText?.textContent.trim(),
                timestamp: new Date().toISOString()
            })
        });
        
        if (response.ok) {
            ToastManager.show(CONSTANTS.MESSAGES.SAVE_SUCCESS);
        } else {
            throw new Error('Save failed');
        }
        */

    // 임시 성공 메시지
    ToastManager.show(CONSTANTS.MESSAGES.SAVE_SUCCESS);
  } catch (error) {
    console.error("저장 실패:", error);
    ToastManager.show(CONSTANTS.MESSAGES.SAVE_FAILED);
  }
}

// 페이지 초기화
function initializePage() {
  PageManager.initializeElements();
  PageManager.showResults();
  KeyboardManager.init();
}

// 페이지 정리
function cleanupPage() {
  KeyboardManager.destroy();
}

// DOM 로드 완료 후 초기화
if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", initializePage);
} else {
  initializePage();
}

// 페이지 언로드 시 정리
window.addEventListener("beforeunload", cleanupPage);

// 전역 함수 노출 (HTML onclick 이벤트용)
window.goToSelection = goToSelection;
window.copyResults = copyResults;
window.shareResults = shareResults;
window.saveResults = saveResults;
