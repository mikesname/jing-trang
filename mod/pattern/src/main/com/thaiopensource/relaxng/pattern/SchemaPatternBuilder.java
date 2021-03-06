package com.thaiopensource.relaxng.pattern;

import com.thaiopensource.xml.util.Name;
import org.relaxng.datatype.Datatype;
import org.xml.sax.Locator;

import java.util.List;

public class SchemaPatternBuilder extends PatternBuilder {
  private boolean idTypes;
  private final UnexpandedNotAllowedPattern unexpandedNotAllowed = new UnexpandedNotAllowedPattern();
  private final TextPattern text = new TextPattern();
  private final PatternInterner schemaInterner = new PatternInterner();

  public SchemaPatternBuilder() { }

  public boolean hasIdTypes() {
    return idTypes;
  }

  Pattern makeElement(NameClass nameClass, Pattern content, Locator loc) {
    Pattern p = new ElementPattern(nameClass, content, loc);
    return schemaInterner.intern(p);
  }

  Pattern makeAttribute(NameClass nameClass, Pattern value, Locator loc) {
    return makeAttribute(nameClass, value, loc, null);
  }
  Pattern makeAttribute(NameClass nameClass, Pattern value, Locator loc, String defaultValue) {
    if (value == notAllowed)
      return value;
    Pattern p = new AttributePattern(nameClass, value, loc, defaultValue);
    return schemaInterner.intern(p);
  }

  Pattern makeData(Datatype dt, Name dtName, List<String> params) {
    noteDatatype(dt);
    Pattern p = new DataPattern(dt, dtName, params);
    return schemaInterner.intern(p);
  }

  Pattern makeDataExcept(Datatype dt, Name dtName, List<String> params, Pattern except, Locator loc) {
    noteDatatype(dt);
    Pattern p = new DataExceptPattern(dt, dtName, params, except, loc);
    return schemaInterner.intern(p);
  }

  Pattern makeValue(Datatype dt, Name dtName, Object value, String stringValue) {
    noteDatatype(dt);
    Pattern p = new ValuePattern(dt, dtName, value, stringValue);
    return schemaInterner.intern(p);
  }

  Pattern makeText() {
    return text;
  }

  Pattern makeOneOrMore(Pattern p) {
    if (p == text)
      return p;
    return super.makeOneOrMore(p);
  }

  Pattern makeUnexpandedNotAllowed() {
    return unexpandedNotAllowed;
  }

  Pattern makeError() {
    Pattern p = new ErrorPattern();
    return schemaInterner.intern(p);
  }

  Pattern makeChoice(Pattern p1, Pattern p2) {
    if (p1 == notAllowed || p1 == p2)
      return p2;
    if (p2 == notAllowed)
      return p1;
    return super.makeChoice(p1, p2);
  }

  Pattern makeList(Pattern p, Locator loc) {
    if (p == notAllowed)
      return p;
    Pattern p1 = new ListPattern(p, loc);
    return schemaInterner.intern(p1);
  }

  Pattern makeMixed(Pattern p) {
    return makeInterleave(text, p);
  }

  private void noteDatatype(Datatype dt) {
    if (dt.getIdType() != Datatype.ID_TYPE_NULL)
      idTypes = true;
  }
}
