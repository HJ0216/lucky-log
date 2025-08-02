// 윤년 체크 함수
function isLeapYear(year) {
  return (year % 4 === 0 && year % 100 !== 0) || year % 400 === 0;
}

// 월별 최대 일수 반환 함수
function getDaysInMonth(year, month) {
  const daysInMonth = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
  if (month === 2 && isLeapYear(year)) {
    return 29;
  }
  return daysInMonth[month - 1];
}

// 일 입력 필드 최대값 업데이트 함수
function updateMaxDay() {
  const yearInput = document.querySelector('[name="year"]');
  const monthInput = document.querySelector('[name="month"]');
  const dayInput = document.querySelector('[name="day"]');

  const year = parseInt(yearInput.value) || 2025;
  const month = parseInt(monthInput.value) || 1;

  if (month >= 1 && month <= 12) {
    const maxDay = getDaysInMonth(year, month);
    dayInput.max = maxDay;

    // 현재 일이 최대값을 초과하면 조정
    if (parseInt(dayInput.value) > maxDay) {
      dayInput.value = maxDay;
    }
  }
}

// 입력값 범위 체크 함수
function validateInput(input, min, max) {
  const value = parseInt(input.value);
  if (value < min) {
    input.value = min;
    // 에러 효과 적용
    input.classList.add("field-error-wiggle");

    // 애니메이션 시간(400ms)이 지난 후 클래스 제거
    setTimeout(() => {
      input.classList.remove("field-error-wiggle");
    }, 1000);
  } else if (value > max) {
    input.value = max;
    input.classList.add("field-error-wiggle");
    setTimeout(() => {
      input.classList.remove("field-error-wiggle");
    }, 1000);
  }
}

// DOM 로드 후 이벤트 리스너 등록
document.addEventListener("DOMContentLoaded", function () {
  const yearInput = document.querySelector('[name="year"]');
  const monthInput = document.querySelector('[name="month"]');
  const dayInput = document.querySelector('[name="day"]');

  // 년도, 월 변경 시 일 최대값 업데이트
  yearInput.addEventListener("change", updateMaxDay);
  monthInput.addEventListener("change", updateMaxDay);

  // 입력값 범위 체크
  yearInput.addEventListener("change", function () {
    validateInput(this, 1900, 2025);
    updateMaxDay();
  });

  monthInput.addEventListener("change", function () {
    validateInput(this, 1, 12);
    updateMaxDay();
  });

  dayInput.addEventListener("change", function () {
    const maxDay = parseInt(this.max) || 31;
    validateInput(this, 1, maxDay);
  });

  // 숫자가 아닌 입력 방지
  [yearInput, monthInput, dayInput].forEach((input) => {
    input.addEventListener("input", function () {
      this.value = this.value.replace(/[^0-9]/g, "");
    });
  });
});

// 폼 검증을 위한 JavaScript
document.querySelector("form").addEventListener("submit", function (e) {
  e.preventDefault();

  // 필수 필드 검증
  const requiredFields = ["gender", "year", "month", "day"];
  let isValid = true;

  requiredFields.forEach((field) => {
    const input = document.querySelector(`[name="${field}"]`);
    if (!input.value) {
      isValid = false;
      input.classList.add("field-error-jump");
    }
  });

  // 날짜 유효성 추가 체크
  const year = parseInt(document.querySelector('[name="year"]').value);
  const month = parseInt(document.querySelector('[name="month"]').value);
  const day = parseInt(document.querySelector('[name="day"]').value);

  if (year < 1900 || year > 2025) {
    isValid = false;
    alert("년도는 1900년부터 2025년까지 입력 가능합니다.");
  } else if (month < 1 || month > 12) {
    isValid = false;
    alert("월은 1월부터 12월까지 입력 가능합니다.");
  } else if (day < 1 || day > getDaysInMonth(year, month)) {
    isValid = false;
    alert(
      `${month}월은 최대 ${getDaysInMonth(year, month)}일까지 입력 가능합니다.`
    );
  }

  if (isValid) {
    alert("사주 분석을 시작합니다! 🔮");
  } else if (
    requiredFields.some(
      (field) => !document.querySelector(`[name="${field}"]`).value
    )
  ) {
    alert("모든 필수 정보를 입력해주세요! ✨");
    setTimeout(() => {
      const currentErrorFields = document.querySelectorAll(".field-error-jump");
      currentErrorFields.forEach((field) =>
        field.classList.remove("field-error-jump")
      );
    }, 500);
  }
});
