'use strict';

const FortuneOptionPage = {
  config: {
    ANIMATION_DURATION: 300, // 0.3s
    ERROR_DURATION: 5000,
    OPTION_BACK_URL: '/fortune/option/back',
    OPTION_DISABLED_TOOLTIP: '준비 중인 기능입니다.',
  },

  // DOM 요소 캐싱
  elements: {
    loadingScreen: null,
    contentsScreen: null,
    form: null,
    submitBtn: null,
    errorMessages: [],
    disabledOptionContainers: [],
  },

  init() {
    this.cacheElements();
    if (!this.validateRequiredElements()) return;
    this.applyInitialStyles();
    this.attachEvents();
    this.startErrorAutoHide();
  },

  cacheElements() {
    this.elements.loadingScreen = document.querySelector('#loading-screen');
    this.elements.contentsScreen = document.querySelector('#contents-screen');
    this.elements.form = document.querySelector('form');
    this.elements.submitBtn = document.querySelector('[data-submit-btn]');
    this.elements.errorMessages = document.querySelectorAll(
      '[data-error-message]'
    );

    // 비활성화된 input을 감싸는 컨테이너 캐싱
    const containers = document.querySelectorAll(
      '.form-option-content-item:has(input:disabled)'
    );

    this.elements.disabledOptionContainers = Array.from(containers);
  },

  validateRequiredElements() {
    const required = ['loadingScreen', 'contentsScreen', 'form', 'submitBtn'];

    const missing = required.filter((key) => !this.elements[key]);
    if (missing.length > 0) {
      const message = `Missing required elements: ${missing.join(', ')}`;

      console.error(message);
      return false;
    }

    return true;
  },

  attachEvents() {
    if (this.elements.form) {
      // this.handleSubmit 메서드를 이벤트 리스너로 등록
      // .bind(this)를 통해 handleSubmit 내부에서 this가 FortuneOptionPage 객체를 가리키도록 함
      this.elements.form.addEventListener(
        'submit',
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
      container.style.opacity = '0.5';
      container.style.cursor = 'not-allowed';
      container.title = this.config.OPTION_DISABLED_TOOLTIP;
    });
  },

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
      this.elements.loadingScreen.style.display = 'flex';
      this.elements.contentsScreen.style.display = 'none';
    }
  },

  // Error
  // 에러 메시지 숨기기
  startErrorAutoHide() {
    this.elements.errorMessages.forEach((msg) => {
      // 메시지에 내용이 있을 때만 타이머 작동
      if (msg.textContent.trim()) {
        setTimeout(() => {
          msg.style.transition = `opacity ${this.config.ANIMATION_DURATION}ms ease-out`;
          msg.style.opacity = '0';

          // fade-out 애니메이션이 끝난 후 display: none 처리
          setTimeout(() => {
            msg.style.display = 'none';
          }, this.config.ANIMATION_DURATION);
        }, this.config.ERROR_DURATION);
      }
    });
  },

  goToBirthInfo() {
    window.location.href = this.config.OPTION_BACK_URL;
  },

  initializePageState() {
    // 로딩 화면 숨기고 컨텐츠 화면 표시
    if (this.elements.loadingScreen) {
      this.elements.loadingScreen.style.display = 'none';
    }
    if (this.elements.contentsScreen) {
      this.elements.contentsScreen.style.display = 'contents';
    }

    // 제출 버튼 활성화
    if (this.elements.submitBtn) {
      this.elements.submitBtn.disabled = false;
    }
  },
};

// 전역 함수 노출 (HTML onclick 이벤트용)
window.goToBirthInfo = () => FortuneOptionPage.goToBirthInfo();

window.addEventListener('pageshow', (event) => {
  // event.persisted가 true이면 bfcache에서 온 것
  if (event.persisted) {
    FortuneOptionPage.initializePageState();
  }
});

document.addEventListener('DOMContentLoaded', () => {
  FortuneOptionPage.init();
});
