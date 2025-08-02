// --- DOM 요소 및 상수 ---
const genderInputs = document.querySelectorAll('[name="gender"]');
const calendarInputs = document.querySelectorAll('[name="calendar"]');
const yearInput = document.querySelector('[name="year"]');
const monthInput = document.querySelector('[name="month"]');
const dayInput = document.querySelector('[name="day"]');

const NUMBER_INPUTS = [yearInput, monthInput, dayInput];
const CURRENT_YEAR = new Date().getFullYear();
const MIN_YEAR = 1940;
const ANIMATION_DURATION = 1000;

// 윤년 체크 함수
function isLeapYear(year) {
  return (year % 4 === 0 && year % 100 !== 0) || year % 400 === 0;
}

// 월별 최대 일수 반환 함수
function getDaysInMonth(year, month) {
  if (month < 1 || month > 12) return 31;

  const daysInMonth = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
  if (month === 2 && isLeapYear(year)) {
    return 29;
  }

  return daysInMonth[month - 1];
}

// 일 입력 필드 최대값 업데이트 함수
function updateMaxDay() {
  const year = parseInt(yearInput.value) || CURRENT_YEAR;
  const month = parseInt(monthInput.value) || new Date().getMonth() + 1;

  if (month >= 1 && month <= 12) {
    const maxDay = getDaysInMonth(year, month);
    dayInput.max = maxDay;

    // 현재 일이 최대값을 초과하면 조정
    if (parseInt(dayInput.value) > maxDay) {
      dayInput.value = maxDay;
    }
  }
}

// 에러 애니메이션 적용 함수
function applyErrorAnimation(input, animationClass) {
  input.classList.add(animationClass);
  setTimeout(() => {
    input.classList.remove(animationClass);
  }, ANIMATION_DURATION);
}

// 입력값 범위 체크 함수
function validateInputRange(input, min, max) {
  const value = parseInt(input.value);
  if (value < min) {
    input.value = min;
    applyErrorAnimation(input, "field-error-wiggle");
  } else if (value > max) {
    input.value = max;
    applyErrorAnimation(input, "field-error-wiggle");
  }
}

// 성별 선택 확인 함수
function isGenderSelected() {
  return Array.from(genderInputs).some((input) => input.checked);
}

// 양력/음력 선택 확인 함수
function isCalendarSelected() {
  return Array.from(calendarInputs).some((input) => input.checked);
}

// 에러 메시지 스택 표시 함수
function showErrorMessages(messages) {
  // 기존 에러 컨테이너 제거
  const existingContainer = document.querySelector(".error-container");
  if (existingContainer) {
    existingContainer.remove();
  }

  if (messages.length === 0) return;

  // 에러 컨테이너 생성
  const errorContainer = document.createElement("div");
  errorContainer.className = "error-container";

  // 각 에러 메시지를 스택으로 추가
  messages.forEach((message, index) => {
    const errorDiv = document.createElement("div");
    errorDiv.className = "error-message";
    errorDiv.textContent = message;

    // 순차적으로 나타나는 애니메이션 효과
    errorDiv.style.animationDelay = `${index * 0.1}s`;

    errorContainer.appendChild(errorDiv);
  });

  // 버튼 컨테이너 위에 삽입
  const btnContainer = document.querySelector(".retro-btn-container");
  btnContainer.parentNode.insertBefore(errorContainer, btnContainer);
}

// 에러 메시지 숨기기 함수
function hideErrorMessages() {
  const errorContainer = document.querySelector(".error-container");
  if (errorContainer) {
    errorContainer.remove();
  }
}

// 전체 폼 유효성 검사 함수
function validateForm() {
  const errors = [];

  // 성별 선택 확인
  if (!isGenderSelected()) {
    errors.push("👶 성별을 선택해주세요!");
    const genderContainer = document.querySelector(".gender-cards");
    if (genderContainer) {
      applyErrorAnimation(genderContainer, "field-error-jump");
    }
  }

  // 양력/음력 선택 확인
  if (!isCalendarSelected()) {
    errors.push("📅 양력 또는 음력을 선택해주세요!");
    const calendarContainer = document.querySelector(".calendar-toggle");
    if (calendarContainer) {
      applyErrorAnimation(calendarContainer, "field-error-jump");
    }
  }

  // 날짜 입력 확인
  const emptyFields = [];
  NUMBER_INPUTS.forEach((input) => {
    if (!input.value.trim()) {
      emptyFields.push(input.placeholder || input.name);
      applyErrorAnimation(input, "field-error-jump");
    }
  });

  if (emptyFields.length > 0) {
    errors.push("🎂 생년월일을 모두 입력해주세요!");
  }

  // 날짜 유효성 확인 (모든 필드가 입력된 경우에만)
  if (emptyFields.length === 0) {
    const year = parseInt(yearInput.value);
    const month = parseInt(monthInput.value);
    const day = parseInt(dayInput.value);

    if (year < MIN_YEAR || year > CURRENT_YEAR) {
      errors.push(
        `📅 년도는 ${MIN_YEAR}년부터 ${CURRENT_YEAR}년까지 입력 가능합니다!`
      );
    }

    if (month < 1 || month > 12) {
      errors.push("📅 월은 1월부터 12월까지 입력 가능합니다!");
    }

    if (year >= MIN_YEAR && year <= CURRENT_YEAR && month >= 1 && month <= 12) {
      const maxDay = getDaysInMonth(year, month);
      if (day < 1 || day > maxDay) {
        errors.push(`📅 ${month}월은 1일부터 ${maxDay}일까지 입력 가능합니다!`);
      }
    }
  }

  return {
    isValid: errors.length === 0,
    errors: errors,
  };
}

// --- 이벤트 리스너 등록 ---
// 년, 월, 일 입력 필터링 (숫자만 입력 가능)
NUMBER_INPUTS.forEach((input) => {
  input.addEventListener("input", function () {
    this.value = this.value.replace(/[^0-9]/g, "");
    hideErrorMessages(); // 입력 시 에러 메시지 숨김
  });
});

// 성별 및 양력/음력 선택 시 에러 메시지 숨기기
[...genderInputs, ...calendarInputs].forEach((input) => {
  input.addEventListener("change", hideErrorMessages);
});

yearInput.addEventListener("change", function () {
  validateInputRange(this, MIN_YEAR, CURRENT_YEAR);
  updateMaxDay();
});

monthInput.addEventListener("change", function () {
  validateInputRange(this, 1, 12);
  updateMaxDay();
});

dayInput.addEventListener("change", function () {
  const maxDay = parseInt(this.max) || 31;
  validateInputRange(this, 1, maxDay);
});

// 폼 제출 이벤트 핸들러
document.querySelector("form").addEventListener("submit", function (e) {
  e.preventDefault();

  const validation = validateForm();

  if (validation.isValid) {
    // 폼 데이터 수집
    const formData = {
      gender: document.querySelector('[name="gender"]:checked').value,
      calendar: document.querySelector('[name="calendar"]:checked').value,
      year: yearInput.value,
      month: monthInput.value,
      day: dayInput.value,
      time: document.querySelector('[name="time"]').value || "",
      city: document.querySelector('[name="city"]').value || "",
    };

    // SessionStorage에 데이터 저장
    sessionStorage.setItem("userFormData", JSON.stringify(formData));

    window.location.href = "/selection.html";
  } else {
    showErrorMessages(validation.errors);
  }
});

// index.html 로드 시 저장된 데이터 복원
document.addEventListener('DOMContentLoaded', function() {
  const savedData = sessionStorage.getItem('userFormData');
  
  if (savedData) {
    const formData = JSON.parse(savedData);
    
    // 저장된 데이터로 폼 복원
    if (formData.gender) {
      document.querySelector(`[name="gender"][value="${formData.gender}"]`).checked = true;
    }
    if (formData.calendar) {
      document.querySelector(`[name="calendar"][value="${formData.calendar}"]`).checked = true;
    }
    if (formData.year) yearInput.value = formData.year;
    if (formData.month) monthInput.value = formData.month;
    if (formData.day) dayInput.value = formData.day;
    if (formData.time) document.querySelector('[name="time"]').value = formData.time;
    if (formData.city) document.querySelector('[name="city"]').value = formData.city;
    
    updateMaxDay(); // 일 최대값 업데이트
  }
});