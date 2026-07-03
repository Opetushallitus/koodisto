export const uniqBy = <T, Key>(items: readonly T[], selector: (item: T) => Key): T[] => {
    const seen = new Set<Key>();
    return items.filter((item) => {
        const key = selector(item);
        if (seen.has(key)) {
            return false;
        }
        seen.add(key);
        return true;
    });
};

export const uniqWith = <T>(items: readonly T[], isEqual: (left: T, right: T) => boolean): T[] =>
    items.reduce<T[]>((uniqueItems, item) => {
        if (!uniqueItems.some((uniqueItem) => isEqual(item, uniqueItem))) {
            uniqueItems.push(item);
        }
        return uniqueItems;
    }, []);

export const capitalize = (value: string): string => {
    const lowerCaseValue = value.toLowerCase();
    return `${lowerCaseValue.charAt(0).toUpperCase()}${lowerCaseValue.slice(1)}`;
};

export type DebouncedFunction<Arguments extends unknown[]> = ((...args: Arguments) => void) & {
    cancel: () => void;
};

export const debounce = <Arguments extends unknown[]>(
    callback: (...args: Arguments) => void,
    wait: number
): DebouncedFunction<Arguments> => {
    let timeout: ReturnType<typeof setTimeout> | undefined;
    const debounced = ((...args: Arguments) => {
        if (timeout) {
            clearTimeout(timeout);
        }
        timeout = setTimeout(() => callback(...args), wait);
    }) as DebouncedFunction<Arguments>;

    debounced.cancel = () => {
        if (timeout) {
            clearTimeout(timeout);
            timeout = undefined;
        }
    };

    return debounced;
};
