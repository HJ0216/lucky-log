'use strict';

const FortuneResultPage = {
  config: {
    INDEX_URL: '/',
    FORTUNE_MY_URL: '/fortune/my',
  },

  messages: {
    copySuccess: '운세 결과가 복사되었습니다! 📋',
    copyFailed: '복사에 실패했습니다. 😅',
    shareSuccess: '운세 결과 공유 페이지가 생성되었습니다! 💌',
    shareFailed: '공유에 실패했습니다. 😅',
    saveSuccess: '운세 결과가 저장되었습니다! 🗂️',
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

      if(!text) {
        toast.error('복사 실패', this.messages.copyFailed);
      }

      navigator.clipboard
        .writeText(text)
        .then(() => {
          toast.success('복사 완료', this.messages.copySuccess);
        })
        .catch((err) => {
          console.error('복사 실패:', err);
          toast.error('복사 실패', this.messages.copyFailed);
        });
    });

    this.elements.shareBtn.addEventListener('click', () => {
      // TODO: 구현 예정
      // 공유할 수 있게 page url을 만드는 방법
    });

    this.elements.saveBtn.addEventListener('click', async () => {
      if (!window.fortuneData) {
        toast.error('저장 실패', this.messages.saveFailed);
        return;
      }

      const {
        birthInfo,
        fortuneOption: option,
        fortuneResultYear,
        responses,
      } = window.fortuneData;

      // TODO: Custom title로 변경 예정
      const titleText =
        document.querySelector('.fortune-title')?.textContent?.trim() || '';
      const subtitleText =
        document.querySelector('.fortune-sub-title')?.textContent?.trim() || '';
      const fullTitle = `${titleText} ${subtitleText}`.trim();

      try {
        const response = await fetch('/api/fortune', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            title: fullTitle,
            birthInfo,
            option,
            fortuneResultYear,
            responses,
          }),
        });

        const data = await response.json();

        if (response.status === 401) {
          toast.error('로그인 필요', this.messages.loginRequired);
          // TODO: 모달 방식으로 변경 예정
          //          setTimeout(() => {
          //            window.location.href = '/login';
          //          }, 1000);
          return;
        }

        if (!response.ok) {
          toast.error('저장 실패', data.message || this.messages.saveFailed);
          return;
        }

        if (data.success) {
          toast.success('저장 완료', this.messages.saveSuccess);
          setTimeout(() => {
            window.location.href = this.config.FORTUNE_MY_URL;
          }, 500);
        } else {
          toast.error('저장 실패', this.messages.saveFailed);
        }
      } catch (error) {
        toast.error('저장 실패', this.messages.saveFailed);
      }
    });
  },

  formatText() {
    try {
      const mainTitle = this.elements.resultScreen
        .querySelector('.fortune-title')
        .textContent.trim();

      const subTitle = this.elements.resultScreen
        .querySelector('.fortune-sub-title')
        .textContent.trim()
        .replace(/\s+/g, '');

      let formattedText = `${mainTitle}: ${subTitle}\n\n`;

      const fortunes = this.elements.resultScreen.querySelectorAll('.fortune-item');

      fortunes.forEach((fortune) => {
        const month = fortune
          .querySelector('.fortune-month')
          .textContent.trim();
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
    } catch (err) {
      console.error('formatText 에러:', err);
      return '';
    }
  },
};

document.addEventListener('DOMContentLoaded', () => {
  FortuneResultPage.init();
});
