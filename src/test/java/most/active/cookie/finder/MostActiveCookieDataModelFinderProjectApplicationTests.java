package most.active.cookie.finder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Map;
import most.active.cookie.finder.exception.BaseException;
import most.active.cookie.finder.model.ArgumentDataModel;
import most.active.cookie.finder.service.CookieFinderService;
import most.active.cookie.finder.service.CookieFinderTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = MostActiveCookieFinderProjectApplication.class,
    args = {"-f", "cookie_log.csv", "-d", "2018-12-09"})
class MostActiveCookieDataModelFinderProjectApplicationTests {

  private static CookieFinderTemplate cookieFinderTemplate;
  private static String[] args = null;
  private static String[] invalidArgs = null;
  private static List<String> lines = null;
  private static Map<Long, List<String>> cookieMap = null;
  private static List<String> searchResult = null;

  @BeforeAll
  public static void setUp() throws BaseException {
    args = new String[4];
    args[0] = "-f";
    args[1] = "cookie_log.csv";
    args[2] = "-d";
    args[3] = "2018-12-09";
    invalidArgs = new String[4];
    invalidArgs[0] = "-d";
    invalidArgs[1] = "cookie_log.csv";
    invalidArgs[2] = "-f";
    invalidArgs[3] = "2018-12-09";
    cookieFinderTemplate = new CookieFinderService();
    lines = cookieFinderTemplate.getOrderedLinesFromFile(args[1]);
    cookieMap = cookieFinderTemplate.getCookieMap(lines);
    searchResult = cookieFinderTemplate.searchMostActiveCookie(cookieMap, args[3]);
  }

  @Test
  void validateArguments() throws BaseException {
    ArgumentDataModel argument = cookieFinderTemplate.argumentValidation(args);
    assertNotNull(argument);
    assertEquals("cookie_log.csv", argument.getFileName());
    assertEquals("2018-12-09", argument.getQueryDate());
  }

  @Test
  void validateIncorrectSearchDateArgument() {
    args[3] = "2018-12/09";
    Assertions.assertThrows(
        BaseException.class, () -> cookieFinderTemplate.argumentValidation(args));
  }

  @Test
  void validateInvalidArguments() {
    Assertions.assertThrows(
        BaseException.class, () -> cookieFinderTemplate.argumentValidation(invalidArgs));
  }

  @Test
  void readRightRowNumberCount() {
    assertNotNull(lines);
    assertEquals(10, lines.size());
  }

  @Test
  void validateCookieMap() {
    assertNotNull(lines);
    assertNotNull(cookieMap);
    assertEquals(10, lines.size());
    assertEquals(3, cookieMap.size());
    assertEquals(1, cookieMap.get(1544140800000L).size());
    assertEquals(6, cookieMap.get(1544313600000L).size());
    assertEquals(3, cookieMap.get(1544227200000L).size());
  }

  @Test
  void validateSearchResult() {
    assertNotNull(searchResult);
    assertEquals(1, searchResult.size());
    assertEquals("AtY0laUfhglK3lC7", searchResult.get(0));
  }
}
