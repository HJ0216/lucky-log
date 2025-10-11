/**
 * IndexPage
 * 인덱스 페이지(사주 정보 입력 폼)의 동적인 UI/UX를 관리하는 모듈 객체
 * - 실시간 입력 값 검증
 * - 동적 날짜 계산
 * - 에러 애니메이션
 */
const IndexPage = {
  // 설정 및 상태
  config: {
    ANIMATION_DURATION: 300, // 0.3s
    ERROR_DURATION: 5000,
    wiggleClass: "wiggle",
  },

  // DOM 요소 캐싱 (Element Cache)
  elements: {
    form: null,
    submitBtn: null,
    yearInput: null,
    monthInput: null,
    dayInput: null,
    timeInput: null,
    cityInput: null,
    dateInputs: [],
    allInputs: [],
    errorContainer: null,
    errorMessages: [],
  },

  // 초기화
  init() {
    this.cacheElements();
    this.attachEvents();
    this.startErrorAutoHide();
  },

  cacheElements() {
    this.elements.form = document.querySelector("form");
    this.elements.submitBtn = document.querySelector("[data-submit-btn]");
    this.elements.yearInput = document.querySelector("#year");
    this.elements.monthInput = document.querySelector("#month");
    this.elements.dayInput = document.querySelector("#day");
    this.elements.timeInput = document.querySelector("#time");
    this.elements.cityInput = document.querySelector("#city");
    this.elements.dateInputs = [
      this.elements.yearInput,
      this.elements.monthInput,
      this.elements.dayInput,
    ];
    this.elements.allInputs = [
      ...this.elements.dateInputs,
      this.elements.timeInput,
      this.elements.cityInput,
    ];
    this.elements.errorContainer = document.querySelector(
      "[data-error-container]"
    );
    this.elements.errorMessages = document.querySelectorAll(
      "[data-error-message]"
    );
  },

  // 캐싱된 DOM 요소들에 필요한 이벤트 리스너를 등록
  attachEvents() {
    // 숫자 입력 필터링
    this.elements.dateInputs.forEach((input) => {
      input.addEventListener("input", (e) => this.filterNumbers(e));
      input.addEventListener("blur", (e) => this.validateRange(e));
    });

    // 입력 시 에러 메시지 숨기기
    this.elements.allInputs.forEach((input) => {
      input.addEventListener("change", () => this.hideErrors());
      input.addEventListener("input", () => this.hideErrors());
    });

    [this.elements.yearInput, this.elements.monthInput].forEach((input) => {
      input.addEventListener("change", () => this.updateDayMaxOnDateChange());
      input.addEventListener("input", () => this.updateDayMaxOnDateChange());
    });

    // 폼 제출 시 로딩 상태
    if (this.elements.form) {
      this.elements.form.addEventListener("submit", () => this.handleSubmit());
    }

    // 옵션 페이지에서 뒤로가기 버튼 클릭 후, 버튼 상태 복원
    window.addEventListener("pageshow", () => this.resetSubmitButton());
  },

  // 검증
  // 숫자만 입력 허용 (실시간 필터링)
  filterNumbers(e) {
    const input = e.target;
    const value = input.value.replace(/\D/g, "");

    if (input.value !== value) {
      input.value = value;
      this.addWiggleAnimation(input);
    }
  },

  // 숫자 입력 필드의 포커스가 해제될 때(blur), min/max 범위를 벗어나는지 검증
  validateRange(e) {
    const input = e.target;
    const value = parseInt(input.value);
    const min = parseInt(input.min);
    let max = parseInt(input.max);

    if (input.id === "day") {
      max = this.getDynamicDayMax();
    }

    if (min && value < min) {
      input.value = min;
      this.addWiggleAnimation(input);
    } else if (max && value > max) {
      input.value = max;
      this.addWiggleAnimation(input);
    }
  },

  // 현재 선택된 년/월을 기준으로 해당 월의 마지막 날짜(28, 29, 30, 31)를 계산
  getDynamicDayMax() {
    const year =
      parseInt(this.elements.yearInput?.value) || new Date().getFullYear();
    const month = parseInt(this.elements.monthInput?.value);

    if (!month || month < 1 || month > 12) {
      return 31;
    }

    const maxDay = new Date(year, month, 0).getDate();

    // 실제 HTML input의 max 속성도 업데이트
    this.elements.dayInput.setAttribute("max", maxDay);

    return maxDay;
  },

  // 년 또는 월 입력값이 변경될 때마다 일(day) 필드의 최대값을 업데이트하고, 현재 입력된 일(day)이 새 최대값을 초과하면 조정
  updateDayMaxOnDateChange() {
    const dayInput = this.elements.dayInput;

    const maxDay = this.getDynamicDayMax();

    // 현재 입력된 일수가 새로운 최대값보다 크면 조정
    const currentDay = parseInt(dayInput.value);
    if (currentDay && currentDay > maxDay) {
      dayInput.value = maxDay;
      this.addWiggleAnimation(dayInput);
    }
  },

  // form
  handleSubmit() {
    const submitBtn = this.elements.submitBtn;

    submitBtn.disabled = true;
    submitBtn.textContent = "처리중...";
  },

  resetSubmitButton() {
    const submitBtn = this.elements.submitBtn;

    submitBtn.disabled = false;
    submitBtn.textContent = "🚀 다음 단계 →";
  },

  // Error
  // 에러 메시지 숨기기
  hideErrors() {
    const container = this.elements.errorContainer;
    if (!container) return;

    container.style.opacity = "0";
    setTimeout(() => {
      container.style.display = "none";
    }, this.config.ANIMATION_DURATION);
  },

  // wiggle animation
  addWiggleAnimation(input) {
    input.classList.add(this.config.wiggleClass);
    setTimeout(() => {
      input.classList.remove(this.config.wiggleClass);
    }, this.config.ANIMATION_DURATION);
  },

  // error message
  startErrorAutoHide() {
    this.elements.errorMessages.forEach((message) => {
      // 메시지에 내용이 있을 때만 타이머 작동
      if (!message.textContent.trim()) return;

      setTimeout(() => {
        message.style.transition = `opacity ${this.config.ANIMATION_DURATION}ms ease-in-out`;
        message.style.opacity = "0";

        // fade-out 애니메이션이 끝난 후 display: none 처리
        setTimeout(() => {
          message.style.display = "none";
        }, this.config.ANIMATION_DURATION);
      }, this.config.ERROR_DURATION);
    });
  },
};

// 페이지 로드 시 초기화
document.addEventListener("DOMContentLoaded", () => {
  IndexPage.init();
});
