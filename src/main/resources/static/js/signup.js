'use strict';

const SignupPage = {
  config: {
    ANIMATION_DURATION: 300, // 0.3s
    ERROR_DURATION: 5000,
    wiggleClass: 'wiggle',
  },

  elements: {
    form: null,
    submitBtn: null,
    emailInput: null,
    passwordInput: null,
    confirmPasswordInput: null,
    nicknameInput: null,
    allInputs: [],
    errorContainer: null,
    errorMessages: [],
  },

  init() {
    this.cacheElements();
    if (!this.validateRequiredElements()) return;
    this.attachEvents();
    this.autoHideErrors();
  },

  cacheElements() {
    this.elements.form = document.querySelector('form');
    this.elements.submitBtn = document.querySelector('[data-submit-btn]');
    this.elements.emailInput = document.querySelector('#email');
    this.elements.passwordInput = document.querySelector('#password');
    this.elements.confirmPasswordInput =
      document.querySelector('#confirmPassword');
    this.elements.nicknameInput = document.querySelector('#nickname');
    this.elements.allInputs = [
      this.elements.emailInput,
      this.elements.passwordInput,
      this.elements.confirmPasswordInput,
      this.elements.nicknameInput,
    ];
    this.elements.errorContainer = document.querySelector(
      '[data-error-container]'
    );
    this.elements.errorMessages = document.querySelectorAll(
      '[data-error-message]'
    );
  },

  validateRequiredElements() {
    const required = [
      'form',
      'submitBtn',
      'emailInput',
      'passwordInput',
      'confirmPasswordInput',
    ];

    const missing = required.filter((key) => !this.elements[key]);
    if (missing.length > 0) {
      const message = `Missing required elements: ${missing.join(', ')}`;

      console.error(message);
      return false;
    }

    return true;
  },

  attachEvents() {
    // 입력 시 에러 메시지 숨기기
    this.elements.allInputs.forEach((input) => {
      input.addEventListener('change', () => this.hideErrors());
      input.addEventListener('input', () => this.hideErrors());
    });
  },

  // Error
  hideErrors() {
    const container = this.elements.errorContainer;
    if (!container) return;

    setTimeout(() => {
      container.classList.add('hidden');
    }, this.config.ANIMATION_DURATION);
  },

  autoHideErrors() {
    this.elements.errorMessages.forEach((message) => {
      // 메시지에 내용이 있을 때만 타이머 작동
      if (!message.textContent.trim()) return;

      // fade-out 애니메이션이 끝난 후 display: none 처리
      setTimeout(() => {
        message.classList.add('hidden');
      }, this.config.ERROR_DURATION);
    });
  },
};

document.addEventListener('DOMContentLoaded', () => {
  SignupPage.init();
});
