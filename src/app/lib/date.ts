import type { Language } from '../translations';

export function formatDate(dateStr?: string, language: Language = 'pt') {
    if (!dateStr) return '';

    const locale = language === 'pt' ? 'pt-BR' : 'en-US';

    // If contains time (ISO with 'T' or space), parse both date and time to avoid timezone offsets
    const hasTime = dateStr.includes('T') || /\d:\d/.test(dateStr);

    if (hasTime) {
        const [datePart, timePartWithZone] = dateStr.includes('T') ? dateStr.split('T') : dateStr.split(' ');
        const dateParts = datePart.split('-');
        if (dateParts.length === 3) {
            const year = Number(dateParts[0]);
            const month = Number(dateParts[1]) - 1;
            const day = Number(dateParts[2]);

            // Extract hh:mm[:ss] ignoring timezone suffix
            const timePart = (timePartWithZone || '').split(/[Z+-]/)[0];
            const timeMatch = timePart.match(/(\d{1,2}):(\d{1,2})(?::(\d{1,2}))?/);
            const hour = timeMatch ? Number(timeMatch[1]) : 0;
            const minute = timeMatch ? Number(timeMatch[2]) : 0;
            const second = timeMatch && timeMatch[3] ? Number(timeMatch[3]) : 0;

            const d = new Date(year, month, day, hour, minute, second);
            return d.toLocaleString(locale);
        }
        // Fallback for other formats with time
        const d = new Date(dateStr);
        return isNaN(d.getTime()) ? dateStr : d.toLocaleString(locale);
    }

    // Date-only (YYYY-MM-DD) handling avoids timezone offsets
    const parts = dateStr.split('-');
    if (parts.length === 3) {
        const year = Number(parts[0]);
        const month = Number(parts[1]) - 1; // monthIndex
        const day = Number(parts[2]);

        const d = new Date(year, month, day);
        return d.toLocaleDateString(locale);
    }

    // Final fallback
    const d = new Date(dateStr);
    return isNaN(d.getTime()) ? dateStr : d.toLocaleDateString(locale);
}
