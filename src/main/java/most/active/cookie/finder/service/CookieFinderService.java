package most.active.cookie.finder.service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import most.active.cookie.finder.exception.BaseException;
import most.active.cookie.finder.exception.ErrorMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CookieFinderService extends CookieFinderTemplate {

  private static Logger logger = LoggerFactory.getLogger(CookieFinderService.class);
  private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  @Override
  public Map<Long, List<String>> getCookieMap(List<String> lines) throws BaseException {
    if (Objects.isNull(lines)) {
      throw new BaseException(ErrorMessages.INCORRECT_FILE);
    }
    Map<Long, List<String>> cookieMap = new HashMap<>();
    lines.forEach(
        line -> {
          String[] data = line.split(",");
          if (Objects.isNull(data) || data.length != 2) {
            try {
              throw new BaseException(ErrorMessages.INCORRECT_FILE_FORMAT);
            } catch (BaseException e) {
              logger.error(e.getMessage());
            }
          }
          String timeStamp = data[1].substring(0, data[1].indexOf('T'));
          LocalDate date = LocalDate.parse(timeStamp, formatter);
          long key = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
          if (cookieMap.get(key) != null) {
            cookieMap.get(key).add(data[0]);
          } else {
            List<String> cookies = new ArrayList<>();
            cookies.add(data[0]);
            cookieMap.put(key, cookies);
          }
        });
    return cookieMap;
  }

  @Override
  public List<String> searchMostActiveCookie(Map<Long, List<String>> cookieMap, String queryDate) {
    long searchKey =
        LocalDate.parse(queryDate, formatter)
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli();
    List<String> result = new ArrayList<>();
    Map<String, Integer> resultMap = new HashMap<>();
    cookieMap.get(searchKey).stream()
        .forEach(s -> resultMap.put(s, Collections.frequency(cookieMap.get(searchKey), s)));
    String key = Collections.max(resultMap.entrySet(), Map.Entry.comparingByValue()).getKey();
    int max = resultMap.get(key);
    resultMap.forEach(
        (s, integer) -> {
          if (integer.equals(max)) {
            result.add(s);
          }
        });
    return result;
  }
}
