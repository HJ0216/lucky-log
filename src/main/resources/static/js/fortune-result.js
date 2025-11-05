'use strict';

const FortuneResultPage = {
  config: {
    INDEX_URL: '/',
  },

  messages: {
    copySuccess: '운세 결과가 복사되었습니다! 📋',
    copyFailed: '복사에 실패했습니다. 😅',
    shareSuccess: '공유 링크가 복사되었습니다! 🔗',
    shareFailed: '공유에 실패했습니다. 😅',
    saveSuccess: '운세가 저장되었습니다! 📂',
    saveFailed: '저장에 실패했습니다. 😅',
    loginRequired: '로그인 페이지로 이동합니다! 🔐',
  },

  // DOM 요소 캐싱
  elements: {
    resultScreen: null,
    retryBtn: null,
    copyBtn: null,
    shareBtn: null,
    saveBtn: null,
  },

  init() {
    this.cacheElements();
    if (!this.validateRequiredElements()) return;
    this.loadMessages();
    this.attachEvents();
  },

  cacheElements() {
    this.elements.resultScreen = document.querySelector('.result-screen');
    this.elements.retryBtn = document.querySelector('[data-retry-btn]');
    this.elements.copyBtn = document.querySelector('[data-copy-btn]');
    this.elements.shareBtn = document.querySelector('[data-share-btn]');
    this.elements.saveBtn = document.querySelector('[data-save-btn]');
  },

  validateRequiredElements() {
    const required = [
      'resultScreen',
      'retryBtn',
      'copyBtn',
      'shareBtn',
      'saveBtn',
    ];

    const missing = required.filter((key) => !this.elements[key]);
    if (missing.length > 0) {
      const message = `Missing required elements: ${missing.join(', ')}`;

      console.error(message);
      return false;
    }

    return true;
  },

  loadMessages() {
    const messageElements = {
      copySuccess: document.getElementById('msg-copy-success'),
      shareSuccess: document.getElementById('msg-share-success'),
      saveSuccess: document.getElementById('msg-save-success'),
      copyFailed: document.getElementById('msg-copy-failed'),
      shareFailed: document.getElementById('msg-share-failed'),
      saveFailed: document.getElementById('msg-save-failed'),
    };

    Object.keys(messageElements).forEach((key) => {
      const element = messageElements[key];
      if (element) {
        this.messages[key] = element.textContent.trim();
      }
    });
  },

  attachEvents() {
    this.elements.retryBtn.addEventListener('click', () => {
      window.location.href = this.config.INDEX_URL;
    });

    this.elements.copyBtn.addEventListener('click', () => {
      const text = this.formatText();

      if (text) {
        navigator.clipboard
          .writeText(text)
          .then(() => {
            toast.success('복사 완료', this.messages.copySuccess);
          })
          .catch((err) => {
            toast.error('복사 실패', this.messages.copyFailed);
          });
      }
    });

    this.elements.shareBtn.addEventListener('click', () => {
      // TODO: 구현 예정
      // 공유할 수 있게 page url을 만드는 방법
    });

    this.elements.saveBtn.addEventListener('click', () => {
      // TODO: 구현 예정
    });
  },

  formatText() {
    const mainTitle = this.elements.resultScreen
      .querySelector('.fortune-title')
      .textContent.trim();

    const resultContent =
      this.elements.resultScreen.querySelector('.result-content');

    const subTitle = resultContent
      .querySelector('.fortune-sub-title')
      .textContent.trim()
      .replace(/\s+/g, '');

    const fortunes = resultContent.querySelectorAll('.fortune-content');

    let formattedText = `${mainTitle}: ${subTitle}\n\n`;

    fortunes.forEach((fortune) => {
      const month = fortune.querySelector('.fortune-month').textContent.trim();
      const content = fortune
        .querySelector('.fortune-month-content')
        .textContent.trim();

      const [title, ...rest] = content.split('\n');
      const description = rest
        .join(' ')
        .replace(/\s+/g, ' ')
        .trim()
        .replace(/\. /g, '.\n');

      formattedText += `${month} ${title}\n${description}\n\n`;
    });

    return formattedText;
  },
};

document.addEventListener('DOMContentLoaded', () => {
  FortuneResultPage.init();
});
