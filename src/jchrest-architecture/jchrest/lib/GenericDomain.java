package jchrest.lib;

import java.util.ArrayList;
import java.util.List;

/**
  * The GenericDomain is used when no domain-specific methods have been created.
  */
public class GenericDomain implements DomainSpecifics {
  public ListPattern normalise (ListPattern pattern) {
    return pattern;
  }

  public List<Square> proposeMovementFixations (Scene scene, Square square) {
    return new ArrayList<Square> ();
  }
}