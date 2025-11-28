'use strict';

const FortuneOptionPage = {
  config: {
    ERROR_DURATION: 5000,
    OPTION_BACK_URL: '/fortune/option/back',
    OPTION_DISABLED_TOOLTIP: '준비 중인 기능입니다.',
  },

  // DOM 요소 캐싱
  elements: {
    loadingScreen: null,
    contentsScreen: null,
    backBtn: null,
    form: null,
    fortuneOptionContainer: null,
    fortuneOptions: null,
    submitBtn: null,
    errorMessages: null,
    disabledOptionContainers: null,
  },

  init() {
    this.cacheElements();
    if (!this.validateRequiredElements()) return;
    this.applyInitialStyles();
    this.attachEvents();
    this.autoHideErrors();
  },

  cacheElements() {
    this.elements.loadingScreen = document.querySelector('#loading-screen');
    this.elements.contentsScreen = document.querySelector('#contents-screen');
    this.elements.backBtn = document.querySelector('[data-back-btn]');
    this.elements.form = document.querySelector('form');
    this.elements.fortuneOptionContainer = document.querySelector('.fortune-type-container');
    this.elements.fortuneOptions = document.querySelectorAll('input[name="fortunes"]');
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
    const required = [
      'loadingScreen',
      'contentsScreen',
      'form',
      'fortuneOptionContainer',
      'fortuneOptions',
      'submitBtn',
      'backBtn',
    ];

    const missing = required.filter((key) => !this.elements[key]);
    if (missing.length > 0) {
      const message = `Missing required elements: ${missing.join(', ')}`;

      console.error(message);
      return false;
    }

    return true;
  },

  applyInitialStyles() {
    this.elements.disabledOptionContainers.forEach((container) => {
      container.classList.add('disabled-option');
      container.title = this.config.OPTION_DISABLED_TOOLTIP;
    });
  },

  attachEvents() {
    this.elements.form.addEventListener('submit', () => this.handleSubmit());
    this.elements.backBtn.addEventListener('click', () => {
      window.location.href = this.config.OPTION_BACK_URL;
    });
    this.elements.fortuneOptionContainer.addEventListener('change', (e) => this.handleFortuneSelection(e));
  },

  handleSubmit() {
    const selectedAI = this.elements.form.querySelector(
      'input[name="ai"]:checked'
    );
    const selectedFortunes = this.elements.form.querySelectorAll(
      'input[name="fortunes"]:checked'
    );
    const selectedPeriod = this.elements.form.querySelector(
      'input[name="period"]:checked'
    );

    // 임시: loading screen이 나오지 않는 오류 확인용
    console.log('loading screen');

    if (!selectedAI || selectedFortunes.length === 0 || !selectedPeriod) {
      // 임시: loading screen이 나오지 않는 오류 확인용
      console.log('unsatisfied validation');
      return;
    }

    this.elements.submitBtn.disabled = true;
    this.elements.loadingScreen.classList.remove('hidden');
    this.elements.contentsScreen.classList.add('hidden');
  },

  handleFortuneSelection(e) {
    if (e.target.name !== 'fortunes') return;
    if (!e.target.checked) return; // 체크 해제 시 무시

    const isOverallSelected = e.target.dataset.isOverall === 'true';

    if (isOverallSelected) {
      // Overall 선택 시: 다른 모든 것 해제
      this.elements.fortuneOptions.forEach(option => {
        if (option !== e.target) {
          option.checked = false;
        }
      });
    } else {
      // 일반 운세 선택 시: Overall만 해제
      this.elements.fortuneOptions.forEach(option => {
        if (option.dataset.isOverall === 'true') {
          option.checked = false;
        }
      });
    }
  },

  // error messages
  autoHideErrors() {
    this.elements.errorMessages.forEach((message) => {
      // 메시지에 내용이 있을 때만 타이머 작동
      if (!message.textContent.trim()) return;

      setTimeout(() => {
        message.classList.add('hidden');
      }, this.config.ERROR_DURATION);
    });
  },

  initializePageState() {
    this.elements.loadingScreen.classList.add('hidden');
    this.elements.contentsScreen.classList.remove('hidden');

    this.elements.submitBtn.disabled = false;
  },
};

document.addEventListener('DOMContentLoaded', () => {
  FortuneOptionPage.init();
});

window.addEventListener('pageshow', (event) => {
  if (event.persisted) {
    FortuneOptionPage.initializePageState();
  }
});
