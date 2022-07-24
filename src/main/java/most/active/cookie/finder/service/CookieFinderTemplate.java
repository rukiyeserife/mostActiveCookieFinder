package most.active.cookie.finder.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import most.active.cookie.finder.exception.BaseException;
import most.active.cookie.finder.exception.ErrorMessages;
import most.active.cookie.finder.model.ArgumentDataModel;

public abstract class CookieFinderTemplate {

  public static final String FIRST_COLUMN = "cookie";
  public static final String SECOND_COLUMN = "timestamp";
  public static final String SEARCH_DATE_FORMAT = "\\d{4}-\\d{2}-\\d{2}";
  public static final String FILE_ARGUMENT = "-f";
  public static final String DATE_ARGUMENT = "-d";

  public ArgumentDataModel argumentValidation(String... args) throws BaseException {
    if (args == null
        || args.length != 4
        || Objects.isNull(args[0])
        || Objects.isNull(args[1])
        || Objects.isNull(args[2])
        || Objects.isNull(args[3])
        || !args[0].equals(FILE_ARGUMENT)
        || !args[2].equals(DATE_ARGUMENT)) {
      throw new BaseException(ErrorMessages.INCORRECT_CALLING);
    }
    var argument = new ArgumentDataModel();
    argument.setFileName(args[1]);
    if (!args[3].matches(SEARCH_DATE_FORMAT)) {
      throw new BaseException(ErrorMessages.INCORRECT_SEARCH_DATE_FORMAT);
    }
    argument.setQueryDate(args[3]);
    return argument;
  }

  public List<String> getOrderedLinesFromFile(String fileName) throws BaseException {
    List<String> lines;
    try {
      lines = Files.readAllLines(Paths.get(fileName));
    } catch (IOException e) {
      throw new BaseException(ErrorMessages.INCORRECT_FILE + fileName);
    }
    if (Objects.isNull(lines)
        || !lines.get(0).contains(FIRST_COLUMN)
        || !lines.get(0).contains(SECOND_COLUMN)) {
      throw new BaseException(ErrorMessages.INCORRECT_FILE_FORMAT);
    }
    lines.remove(0);
    return lines;
  }

  public abstract Map<Long, List<String>> getCookieMap(List<String> lines) throws BaseException;

  public abstract List<String> searchMostActiveCookie(
      Map<Long, List<String>> cookieMap, String queryDate);

  public void cookieFinderRun(String... args) throws BaseException {
    ArgumentDataModel argument = argumentValidation(args);
    if (Objects.nonNull(argument)) {
      List<String> lines = getOrderedLinesFromFile(argument.getFileName());
      Map<Long, List<String>> cookieMap = getCookieMap(lines);
      searchMostActiveCookie(cookieMap, argument.getQueryDate()).stream()
          .forEach(System.out::println);
    }
  }
}
