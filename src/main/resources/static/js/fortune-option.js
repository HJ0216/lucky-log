"use strict";

/**
 * FortuneOptionPage
 * 운세 옵션 페이지의 UI/UX를 관리하는 모듈 객체
 * - 폼 제출 로직
 * - 에러 메시지 처리
 * - 비활성화 요소 스타일링
 */
const FortuneOptionPage = {
  // 설정값
  config: {
    selectionPageUrl: "/fortune/option/back",
    errorHideDelay: 1000, // ms (1초)
    fadeoutDuration: 300, // ms (0.3초)
    disabledOptionTooltip: "준비 중인 기능입니다.",
  },

  // DOM 요소 캐싱: 반복적인 DOM 탐색을 피해 성능을 향상
  elements: {
    loadingScreen: null,
    contentsScreen: null,
    form: null,
    submitBtn: null,
    errorMessages: [],
    disabledOptionContainers: [],
  },

  /**
   * 모듈 초기화 메서드
   * 페이지 로드 시 호출되어 모든 기능을 활성화
   */
  init() {
    this.cacheElements();
    this.applyInitialStyles();
    this.attachEvents();
    this.startErrorAutoHide();
  },

  /**
   * 필요한 DOM 요소를 찾아 캐싱
   */
  cacheElements() {
    this.elements.loadingScreen = document.getElementById("loading-screen");
    this.elements.contentsScreen = document.getElementById("contents-screen");
    this.elements.form = document.querySelector("form");
    if (this.elements.form) {
      this.elements.submitBtn = this.elements.form.querySelector(".retro-btn");
    }
    this.elements.errorMessages = document.querySelectorAll(
      ".error-message, .alert"
    );

    // 비활성화된 input을 감싸는 컨테이너를 직접 캐싱
    document.querySelectorAll("input:disabled").forEach((input) => {
      const container = input.closest(
        ".ai-option, .fortune-option, .period-option"
      );
      if (container) {
        this.elements.disabledOptionContainers.push(container);
      }
    });
  },

  /**
   * 이벤트 리스너를 등록
   */
  attachEvents() {
    if (this.elements.form) {
      // this.handleSubmit 메서드를 이벤트 리스너로 등록
      // .bind(this)를 통해 handleSubmit 내부에서 this가 FortuneOptionPage 객체를 가리키도록 함
      this.elements.form.addEventListener(
        "submit",
        this.handleSubmit.bind(this)
      );
    }
  },

  /**
   * 페이지 로드 시 초기 UI 상태를 설정
   * (예: 비활성화된 옵션 스타일링)
   */
  applyInitialStyles() {
    this.elements.disabledOptionContainers.forEach((container) => {
      container.style.opacity = "0.5";
      container.style.cursor = "not-allowed";
      container.title = this.config.disabledOptionTooltip;
    });
  },

  /**
   * 폼 제출 시 실행될 핸들러
   */
  handleSubmit() {
    const selectedAI = this.elements.form.querySelectorAll(
      'input[name="ai"]:checked'
    );
    const selectedFortunes = this.elements.form.querySelectorAll(
      'input[name="fortunes"]:checked'
    );
    const selectedPeriod = this.elements.form.querySelectorAll(
      'input[name="period"]:checked'
    );

    if (
      selectedAI.length === 0 ||
      selectedFortunes.length === 0 ||
      selectedPeriod.length === 0
    ) {
      return;
    }

    if (this.elements.submitBtn) {
      this.elements.submitBtn.disabled = true;
      this.elements.loadingScreen.style.display = "flex";
      this.elements.contentsScreen.style.display = "none";
    }
  },

  /**
   * 에러 메시지를 일정 시간 후 자동으로 숨기는 로직
   */
  startErrorAutoHide() {
    this.elements.errorMessages.forEach((msg) => {
      // 메시지에 내용이 있을 때만 타이머 작동
      if (msg.textContent.trim()) {
        setTimeout(() => {
          msg.style.transition = `opacity ${this.config.fadeoutDuration}ms ease-out`;
          msg.style.opacity = "0";

          // fade-out 애니메이션이 끝난 후 display: none 처리
          setTimeout(() => {
            msg.style.display = "none";
          }, this.config.fadeoutDuration);
        }, this.config.errorHideDelay);
      }
    });
  },

  /**
   * 생년월일 선택 페이지로 이동
   */
  goToBirthInfo() {
    window.location.href = this.config.selectionPageUrl;
  },

  initializePageState() {
    // 로딩 화면 숨기고 컨텐츠 화면 표시
    if (this.elements.loadingScreen) {
      this.elements.loadingScreen.style.display = "none";
    }
    if (this.elements.contentsScreen) {
      this.elements.contentsScreen.style.display = "contents";
    }

    // 제출 버튼 활성화
    if (this.elements.submitBtn) {
      this.elements.submitBtn.disabled = false;
    }
  },
};

// 전역 함수 노출 (HTML onclick 이벤트용)
window.goToBirthInfo = () => FortuneOptionPage.goToBirthInfo();

window.addEventListener("pageshow", (event) => {
  // event.persisted가 true이면 bfcache에서 온 것
  if (event.persisted) {
    FortuneOptionPage.initializePageState();
  }
});

// 페이지의 모든 DOM 콘텐츠가 로드된 후 모듈을 초기화
document.addEventListener("DOMContentLoaded", () => {
  FortuneOptionPage.init();
});
