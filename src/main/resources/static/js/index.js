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
    animationDuration: 1000,
    errorClass: "field-error-jump",
    wiggleClass: "field-error-wiggle",
  },

  // DOM 요소 캐싱 (Element Cache)
  elements: {
    form: null,
    numberInputs: [],
    allInputs: [],
    yearInput: null,
    monthInput: null,
    dayInput: null,
  },

  // 초기화
  init() {
    this.cacheElements();
    this.attachEvents();
    this.showErrorAnimations(); // 서버에서 온 에러들에 애니메이션 적용
  },

  cacheElements() {
    this.elements.form = document.querySelector("form");
    this.elements.numberInputs = document.querySelectorAll(
      'input[type="number"]'
    );
    this.elements.allInputs = document.querySelectorAll(
      'input[type="radio"], input[type="checkbox"], input[type="number"], input[type="text"], select'
    );
  },

  // 캐싱된 DOM 요소들에 필요한 이벤트 리스너를 등록
  attachEvents() {
    // 숫자 입력 필터링 (실시간 UX)
    this.elements.numberInputs.forEach((input) => {
      input.addEventListener("input", this.filterNumbers.bind(this));
      input.addEventListener("blur", this.validateRange.bind(this));
    });

    // 폼 제출 시 로딩 상태
    if (this.elements.form) {
      this.elements.form.addEventListener(
        "submit",
        this.handleSubmit.bind(this)
      );
    }

    // 입력 시 에러 메시지 숨기기 (UX)
    this.elements.allInputs.forEach((input) => {
      input.addEventListener("change", this.hideErrors.bind(this));
      input.addEventListener("input", this.hideErrors.bind(this));
    });

    const yearInput = document.querySelector('input[name="year"]');
    const monthInput = document.querySelector('input[name="month"]');

    if (yearInput) {
      yearInput.addEventListener(
        "change",
        this.updateDayMaxOnDateChange.bind(this)
      );
      yearInput.addEventListener(
        "input",
        this.updateDayMaxOnDateChange.bind(this)
      );
    }

    if (monthInput) {
      monthInput.addEventListener(
        "change",
        this.updateDayMaxOnDateChange.bind(this)
      );
      monthInput.addEventListener(
        "input",
        this.updateDayMaxOnDateChange.bind(this)
      );
    }
  },

  // 검증
  // 숫자만 입력 허용 (실시간 필터링)
  filterNumbers(e) {
    const input = e.target;
    const value = input.value.replace(/[^0-9]/g, "");

    if (input.value !== value) {
      input.value = value;
      this.wiggleInput(input);
    }
  },

  // 숫자 입력 필드의 포커스가 해제될 때(blur), min/max 범위를 벗어나는지 검증
  validateRange(e) {
    const input = e.target;
    const value = parseInt(input.value);
    const min = parseInt(input.min);
    let max = parseInt(input.max);

    if (input.name === "day") {
      max = this.getDynamicDayMax();
    }

    if (value != null && min && value < min) {
      input.value = min;
      this.wiggleInput(input);
    } else if (value && max && value > max) {
      input.value = max;
      this.wiggleInput(input);
    }
  },

  // 현재 선택된 년/월을 기준으로 해당 월의 마지막 날짜(28, 29, 30, 31)를 계산
  getDynamicDayMax() {
    const yearInput = document.querySelector('input[name="year"]');
    const monthInput = document.querySelector('input[name="month"]');

    const year = parseInt(yearInput?.value) || new Date().getFullYear();
    const month = parseInt(monthInput?.value);

    if (!month || month < 1 || month > 12) {
      return 31;
    }

    const maxDay = new Date(year, month, 0).getDate();

    // 실제 HTML input의 max 속성도 업데이트
    const dayInput = document.querySelector('input[name="day"]');
    if (dayInput) {
      dayInput.setAttribute("max", maxDay);
    }

    return maxDay;
  },

  // 년 또는 월 입력값이 변경될 때마다 일(day) 필드의 최대값을 업데이트하고, 현재 입력된 일(day)이 새 최대값을 초과하면 조정
  updateDayMaxOnDateChange() {
    const dayInput = document.querySelector('input[name="day"]');
    if (dayInput) {
      const maxDay = this.getDynamicDayMax();
      dayInput.setAttribute("max", maxDay);

      // 현재 입력된 일수가 새로운 최대값보다 크면 조정
      const currentDay = parseInt(dayInput.value);
      if (currentDay && currentDay > maxDay) {
        dayInput.value = maxDay;
        this.wiggleInput(dayInput);
      }
    }
  },

  // form
  // 폼 제출 처리 (로딩 상태만)
  handleSubmit(e) {
    const submitBtn = this.elements.form.querySelector('button[type="submit"]');
    if (submitBtn) {
      submitBtn.disabled = true;
      submitBtn.textContent = "처리중...";
    }
  },

  // Error
  // 에러 메시지 숨기기
  hideErrors() {
    const errorContainer = document.querySelector(".error-container");
    if (errorContainer) {
      errorContainer.style.opacity = "0";
      setTimeout(() => {
        errorContainer.style.display = "none";
      }, 300);
    }
  },

  // 서버에서 전달된 에러 필드들에 애니메이션 적용
  showErrorAnimations() {
    if (window.errorFields && window.errorFields.length > 0) {
      window.errorFields.forEach((fieldName) => {
        this.animateErrorField(fieldName);
      });
    }
  },

  // 필드별 에러 애니메이션
  animateErrorField(fieldName) {
    let element = null;

    // 필드에 따라 적절한 요소 선택
    if (fieldName === "gender") {
      element = document.querySelector(".gender-cards");
    } else if (fieldName === "calendar") {
      element = document.querySelector(".calendar-toggle");
    } else if (
      fieldName === "year" ||
      fieldName === "month" ||
      fieldName === "day"
    ) {
      element = document.querySelector(".date-inputs");
    } else {
      element = document.querySelector(`[name="${fieldName}"]`);
    }

    if (element) {
      this.jumpAnimation(element);
    }
  },

  // 애니메이션
  // 점프 애니메이션
  jumpAnimation(element) {
    element.classList.add(this.config.errorClass);
    setTimeout(() => {
      element.classList.remove(this.config.errorClass);
    }, this.config.animationDuration);
  },

  // 흔들기 애니메이션
  wiggleInput(input) {
    input.classList.add(this.config.wiggleClass);
    setTimeout(() => {
      input.classList.remove(this.config.wiggleClass);
    }, this.config.animationDuration);
  }
};

// 페이지 로드 시 초기화
document.addEventListener("DOMContentLoaded", () => {
  IndexPage.init();
});
