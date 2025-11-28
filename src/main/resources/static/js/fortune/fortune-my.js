'use strict';

const FortuneMyPage = {
  config: {
    ERROR_DURATION: 5000,
  },

  // DOM 요소 캐싱
  elements: {
      errorMessage: null,
  },

  init() {
    this.cacheElements();
    this.attachEvents();
    this.autoHideErrors();
  },

  cacheElements() {
    this.elements.errorMessage = document.querySelector(
      '[data-error-message]'
    );
  },

  attachEvents() {},

  autoHideErrors() {
    setTimeout(() => {
      this.elements.errorMessage.classList.add('hidden');
    }, this.config.ERROR_DURATION);
  },
};

document.addEventListener('DOMContentLoaded', () => {
  FortuneMyPage.init();
});
