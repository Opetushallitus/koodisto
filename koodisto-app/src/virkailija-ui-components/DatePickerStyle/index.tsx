import '@daypicker/react/style.css';

import { createGlobalStyle } from 'styled-components';

const DatePickerStyle = createGlobalStyle`
  .DatePicker__ {
    --rdp-accent-color: ${({ theme }) => theme.colors.primary.main};
    --rdp-accent-background-color: ${({ theme }) => theme.colors.primary.focusOutline};
    --rdp-day_button-border-radius: ${({ theme }) => theme.radii[1]}px;

    font-family: ${({ theme }) => theme.fonts.main};

    .rdp-caption_label,
    .rdp-day_button {
      color: ${({ theme }) => theme.colors.text.primary};
    }

    .rdp-day_button:hover:not(:disabled) {
      background-color: rgba(0, 0, 0, .05) !important;
    }
    
    .rdp-today:not(.rdp-outside) {
      color: ${({ theme }) => theme.colors.primary.main};
      font-weight: normal !important;
    }

    .rdp-selected .rdp-day_button {
      background-color: ${({ theme }) => theme.colors.primary.main};
      border-color: ${({ theme }) => theme.colors.primary.main};
      color: ${({ theme }) => theme.colors.primary.contrastText};
      font-weight: ${({ theme }) => theme.fontWeights.bold} !important;
    }

    .rdp-weekday {
      color: ${({ theme }) => theme.colors.text.secondary} !important;
      opacity: 1;
    }
  }

  .DatePickerOverlay__ {
    border: 1px solid ${({ theme }) => theme.colors.divider};
    border-radius: ${({ theme }) => theme.radii[1]}px;
    box-shadow: ${({ theme }) => theme.shadows.dropdownMenu};
    background-color: white;
  }

  .DatePickerOverlayWrapper__ {
    display: inline-block !important;
    transform: translateY(${({ theme }) => theme.space[1]}px);
    position: absolute;
    z-index: ${({ theme }) => theme.zIndices.datePicker};
  }
`;

export default DatePickerStyle;
