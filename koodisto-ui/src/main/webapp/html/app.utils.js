// Initial values that might be overriden when /configurations/* are loaded.
import naturalSort from "javascript-natural-sort";

export const koodistoConfig = {
    SERVICE_NAME: "APP_KOODISTO",
    SERVICE_URL_BASE: '/',
    SESSION_KEEPALIVE_INTERVAL_IN_SECONDS: 30,
    MAX_SESSION_IDLE_TIME_IN_SECONDS: 1800,
};

export const getLanguageSpecificValue = (fieldArray, fieldName, language) => {
    if (fieldArray) {
        for (let i = 0; i < fieldArray.length; i++) {
            if (fieldArray[i].kieli === language) {
                const result = eval("fieldArray[i]." + fieldName);
                return result === null ? "" : result;
            }
        }
    }
    return "";
};

export const getLanguageSpecificValueOrValidValue = (fieldArray, fieldName, language) => {
    let specificValue = getLanguageSpecificValue(fieldArray, fieldName, language);

    if (specificValue === "" && language !== "FI") {
        specificValue = getLanguageSpecificValue(fieldArray, fieldName, "FI");
    }
    if (specificValue === "" && language !== "SV") {
        specificValue = getLanguageSpecificValue(fieldArray, fieldName, "SV");
    }
    if (specificValue === "" && language !== "EN") {
        specificValue = getLanguageSpecificValue(fieldArray, fieldName, "EN");
    }
    return specificValue;
};

export const NaturalSortFilter = () => {
    return function (arrInput, field, reverse) {
        const arr = arrInput.sort(function (a, b) {
            let valueA = field ? a[field] : a;
            let valueB = field ? b[field] : b;
            const aIsString = typeof valueA === 'string';
            const bIsString = typeof valueB === 'string';
            return naturalSort(aIsString ? valueA.trim().toLowerCase() : valueA, bIsString ? valueB.trim().toLowerCase() : valueB);
        });
        return reverse ? arr.reverse() : arr;
    };
};

