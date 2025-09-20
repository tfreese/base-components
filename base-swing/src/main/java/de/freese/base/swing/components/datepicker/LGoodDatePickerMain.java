// Created: 13.11.22
package de.freese.base.swing.components.datepicker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.time.DayOfWeek;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.github.lgooddatepicker.components.CalendarPanel;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.CalendarListener;
import com.github.lgooddatepicker.zinternaltools.CalendarSelectionEvent;
import com.github.lgooddatepicker.zinternaltools.HighlightInformation;
import com.github.lgooddatepicker.zinternaltools.YearMonthChangeEvent;

/**
 * Alternative: <a href="https://github.com/LGoodDatePicker/LGoodDatePicker">LGoodDatePicker</a>
 *
 * @author Thomas Freese
 */
public final class LGoodDatePickerMain {
    static void main() {
        final DatePickerSettings datePickerSettings = new DatePickerSettings();
        datePickerSettings.setFirstDayOfWeek(DayOfWeek.MONDAY);
        datePickerSettings.setWeekNumbersDisplayed(true, true);
        datePickerSettings.setColor(DatePickerSettings.DateArea.TextMonthAndYearMenuLabels, Color.BLUE); // Like clickable Links.
        datePickerSettings.setColor(DatePickerSettings.DateArea.TextTodayLabel, Color.BLUE); // Like clickable Link.
        datePickerSettings.setVisibleClearButton(false);
        datePickerSettings.setAllowEmptyDates(false);
        datePickerSettings.setSizeDatePanelMinimumHeight(180);

        // Adjust Background for Days with HighlightPolicy, otherwise they are Turquoise.
        datePickerSettings.setColor(DatePickerSettings.DateArea.CalendarDefaultBackgroundHighlightedDates,
                datePickerSettings.getColor(DatePickerSettings.DateArea.CalendarBackgroundNormalDates));
        datePickerSettings.setHighlightPolicy(localDate -> {
            final DayOfWeek dayOfWeek = localDate.getDayOfWeek();

            if (DayOfWeek.SUNDAY.equals(dayOfWeek)) {
                return new HighlightInformation(null, Color.RED);
            }

            return null;
        });

        final CalendarPanel calendarPanel = new CalendarPanel(datePickerSettings);
        calendarPanel.addCalendarListener(new CalendarListener() {
            @Override
            public void selectedDateChanged(final CalendarSelectionEvent event) {
                System.out.println("selectedDateChanged: " + event.getNewDate());
            }

            @Override
            public void yearMonthChanged(final YearMonthChangeEvent event) {
                System.out.println("yearMonthChanged: " + event.getNewYearMonth());
            }
        });

        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(BorderLayout.CENTER, calendarPanel);

        // frame.setSize(300, 300);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private LGoodDatePickerMain() {
        super();
    }
}
