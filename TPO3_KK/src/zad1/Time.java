/**
 *
 *  @author Kłodawski Kamil S24777
 *
 */

package zad1;


import java.time.*;
import java.time.format.*;
import java.util.Locale;

public class Time {

    private static String spelling(int num, String format1, String format2, String format3) {
        if(num == 1) return format1;
        else if (num % 10 >= 2 && num % 10 <= 4 && (num % 100 < 10 || num % 100 >= 20)) return format2;
        else return format3;
    }
    public static String passed(String from, String to) {

        try {
            //ZonedDateTime zone = ZonedDateTime.now(ZoneId.of("Europe/Warsaw"));
            DateTimeFormatter ISOFormatter = new DateTimeFormatterBuilder()
                    .append(DateTimeFormatter.ISO_LOCAL_DATE)
                    //.appendPattern("yyyy-MM-dd")
                    .optionalStart()
                    .appendLiteral('T')
                    .appendPattern("HH:mm")
                    //.appendOffset("+HH:MM", "Z")
                    .optionalEnd()
                    .toFormatter()
                    .withResolverStyle(ResolverStyle.STRICT)
                    .withZone(ZoneId.of("Europe/Warsaw"));
            //ISOFormatter.format(zone);
            LocalDateTime parsedFrom2;
            LocalDateTime parsedTo2;

            //ZonedDateTime parsedFrom = ZonedDateTime.parse(from, ISOFormatter.withZone(ZoneId.of("Europe/Warsaw")));
            //ZonedDateTime parsedTo = ZonedDateTime.parse(to, ISOFormatter.withZone(ZoneId.of("Europe/Warsaw")));
            ZonedDateTime parsedFrom;
            ZonedDateTime parsedTo;
            boolean containHrs = false;
            if(ISOFormatter.parseBest(from, LocalDateTime::from, LocalDate::from) instanceof LocalDateTime){
               parsedFrom2 = LocalDateTime.parse(from,ISOFormatter);
               parsedFrom = ZonedDateTime.parse(from, ISOFormatter);
               containHrs = true;
            } else {
                parsedFrom2 = LocalDate.parse(from,ISOFormatter).atStartOfDay();
                parsedFrom = LocalDate.parse(from, ISOFormatter).atStartOfDay(ZoneId.of("Europe/Warsaw")).withZoneSameInstant(ZoneOffset.UTC);
            }
            if(ISOFormatter.parseBest(to, LocalDateTime::from, LocalDate::from) instanceof LocalDateTime){
                parsedTo2 = LocalDateTime.parse(to,ISOFormatter);
                parsedTo = ZonedDateTime.parse(to, ISOFormatter);
                containHrs = true;
            } else {
                parsedTo2 = LocalDate.parse(to,ISOFormatter).atStartOfDay();
                parsedTo = LocalDate.parse(to, ISOFormatter).atStartOfDay(ZoneId.of("Europe/Warsaw")).withZoneSameInstant(ZoneOffset.UTC);
            }

            Duration dur = Duration.between(parsedFrom2,parsedTo2);
            Period per = Period.between(parsedFrom.toLocalDate(),parsedTo.toLocalDate());
            Locale loc = Locale.forLanguageTag("pl");

            String passed = String.format("Od %d %s %d (%s) ",
                    parsedFrom2.getDayOfMonth(), parsedFrom2.getMonth().getDisplayName(TextStyle.FULL,loc), parsedFrom2.getYear(), parsedFrom2.getDayOfWeek().getDisplayName(TextStyle.FULL,loc)
                    );
            if(containHrs) {
                passed += String.format("godz. %d:%s ",parsedFrom2.getHour(),parsedFrom2.getMinute() == 0 ? "00" : parsedFrom2.getMinute());
            }
            passed += String.format("do %d %s %d (%s) ",
                    parsedTo2.getDayOfMonth(), parsedTo2.getMonth().getDisplayName(TextStyle.FULL,loc), parsedTo2.getYear(), parsedTo2.getDayOfWeek().getDisplayName(TextStyle.FULL,loc));
            if(containHrs) {
                passed += String.format("godz. %d:%s ",parsedTo2.getHour(),parsedTo2.getMinute() == 0 ? "00" : parsedTo2.getMinute());
            }
            passed += String.format("\n- mija: %d dni, tygodni %s",
                    dur.toDays(), dur.toDays()/7.0d == dur.toDays()/7 ? String.format(Locale.US, "%d", dur.toDays()/7) : String.format(Locale.US, "%.2f", dur.toDays()/7.0d));
            if(containHrs) {
                Duration dur2 = Duration.between(parsedFrom,parsedTo);
                passed += String.format("\n- godzin: %d, minut %d",
                        dur2.toHours(), dur2.toMinutes()
                        );
            }

            if(dur.toDays() >= 1) {
                passed += "\n- kalendarzowo: ";

                if(per.getYears() >= 1) passed += String.format("%d %s", per.getYears(), spelling(per.getYears(), "rok", "lata", "lat"));
                if((per.getYears() >= 1 && per.getMonths() >= 1) || (per.getYears() >= 1 && per.getDays() > 0)) passed += ", ";
                if(per.getMonths() >= 1) passed += String.format("%d %s", per.getMonths(), spelling(per.getYears(), "miesiac", "miesiace", "miesiecy"));
                if(per.getMonths() >= 1 && per.getDays() > 0) passed += ", ";
                if(per.getDays() > 0) passed += String.format("%d %s", per.getDays(), spelling(per.getDays(), "dzień", "dni", "dni"));
            }

            return passed;
        } catch (Exception e) {
            return "*** " + e.toString();
        }

    }
}
