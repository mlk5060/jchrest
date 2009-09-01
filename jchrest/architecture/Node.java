package jchrest.architecture;

import java.util.ArrayList;
import java.util.List;

import jchrest.lib.ListPattern;
import jchrest.lib.Pattern;

/**
 * Represents a node within the model's discrimination network.
 * Methods support learning and also display.
 *
 * @author Peter C. R. Lane
 */
public class Node {
  private static int _totalNodes = 0;
  private int _reference;
  private ListPattern _contents;
  private ListPattern _image;
  private List<Link> _children;
  private Node _followedBy;

  /**
   * Empty constructor is only called to construct a new root node for the 
   * model.  
   */
  public Node () {
    this (Pattern.makeList (new String[]{}), Pattern.makeList (new String[]{}));
    _totalNodes = 1;
    _reference = 0;
  }
 
  /**
   * When constructing non-root nodes in the network, the new contents and image 
   * must be defined.  Assume that the image always starts empty.
   */
  public Node (ListPattern contents, ListPattern image) {
    _reference = _totalNodes;
    _totalNodes += 1;

    _contents = contents.clone ();
    _contents.setNotFinished (); // do not allow contents to be finished
    _image = image;
    _children = new ArrayList<Link> ();
    _followedBy = null;
  }

  /**
   * Accessor to reference number of node.
   */
  public int getReference () {
    return _reference;
  }

  /**
   * Accessor to contents of node.
   */
  public ListPattern getContents () {
    return _contents;
  }

  /**
   * Accessor to image of node.
   */
  public ListPattern getImage () {
    return _image;
  }

  /**
   * Accessor to children of node.
   */
  public List<Link> getChildren () {
    return _children;
  }

  /**
   * Accessor to node that follows this node.
   */
  public Node getFollowedBy () {
    return _followedBy;
  }

  /**
   * Modify node that follows this node.
   */
  public void setFollowedBy (Node node) {
    _followedBy = node;
  }

  /** 
   * Compute the size of the network below the current node.
   */
  public int size () {
    int count = 1; // for self
    for (Link link : _children) {
      count += link.getChildNode().size ();
    }

    return count;
  }

  /**
   * LearnPrimitive is used to construct a test link and node containing 
   * precisely the given pattern.  It is assumed the given pattern contains 
   * a single primitive item, and is finished.
   */
  public Node learnPrimitive (Chrest model, ListPattern pattern) {
    assert (pattern.isFinished () && pattern.size () == 1);
    ListPattern contents = pattern.clone ();
    contents.setNotFinished ();
    Node child = new Node (contents, pattern);
    _children.add (0, new Link (contents, child));
    model.advanceClock (model.getDiscriminationTime ());

    return child;
  }

  /**
   * Discrimination learning extends the LTM network by adding new 
   * nodes.
   */
  Node discriminate (Chrest model, ListPattern pattern) {
    ListPattern newInformation = pattern.remove (_contents);

    // nothing to learn from, so stop
    if (newInformation.isEmpty ()) {
      if (newInformation.isFinished ()) { // add test for < $ >
        ListPattern newContents = _contents.clone ();
        newContents.setFinished ();
        Node newChild = new Node (newContents, new ListPattern ());
        ListPattern finishTest = new ListPattern ();
        finishTest.setFinished ();
        _children.add (0, new Link (finishTest, newChild));
        model.advanceClock (model.getDiscriminationTime ());
        return newChild;
      }
      return this;
    }

    Node retrievedChunk = model.recognise (pattern);
    if (retrievedChunk == model.getLtm ()) {
      // if root node is retrieved, then the primitive must be learnt
      return model.getLtm().learnPrimitive (model, pattern.getFirstItem ());
    } else if (retrievedChunk.getImage().isEmpty ()) {
      // if the retrieved chunk has an empty image, then familiarisation must occur
      // to extend that image.
      return retrievedChunk.familiarise (model, pattern);
    } else if (retrievedChunk.getImage().matches (newInformation)) {
      // else, create a new test link using the provided chunk as a test
      Node newChild = new Node (_contents.append (retrievedChunk.getImage ()), new ListPattern ());
      ListPattern contents = retrievedChunk.getImage ().clone ();
      contents.setNotFinished ();
      _children.add (0, new Link (contents, newChild));
      model.advanceClock (model.getDiscriminationTime ());
      return newChild;
    } else { // look for or learn a new primitive chunk
      ListPattern primitive = newInformation.getFirstItem ();
      retrievedChunk = model.recognise (primitive);
      if (retrievedChunk == model.getLtm ()) {
        return model.getLtm().learnPrimitive (model, primitive);
      } else {
        Node newChild = new Node (_contents.append (primitive), new ListPattern ());
        ListPattern contents = primitive.clone ();
        contents.setNotFinished ();
        _children.add (0, new Link (contents, newChild));
        model.advanceClock (model.getDiscriminationTime ());
        return newChild;
      }
    }
  }

  /**
   * Familiarisation learning extends the image in a node by adding new 
   * information from the given pattern.
   */
  Node familiarise (Chrest model, ListPattern pattern) {
    ListPattern newInformation = pattern.remove (_image);

    if (newInformation.isEmpty ()) {
      // if there is no more information, check if new pattern is 'complete'
      // and make this image complete, if so.
      if (newInformation.isFinished ()) {
        _image.setFinished ();
      }
    } else {
      Node retrievedChunk = model.recognise (newInformation);
      if (retrievedChunk == model.getLtm ()) {
        return model.getLtm().learnPrimitive (model, newInformation.getFirstItem ());
      } else if (retrievedChunk.getImage().isEmpty ()) {
        // could not retrieve useful chunk, so look for, or learn, a primitive
        ListPattern primitive = newInformation.getFirstItem ();
        retrievedChunk = model.recognise (primitive);
        if (retrievedChunk == model.getLtm ()) {
          return model.getLtm().learnPrimitive (model, primitive);
        } else {
          ListPattern toadd = primitive.clone ();
          toadd.setNotFinished ();
          _image = _image.append (toadd);
          model.advanceClock (model.getFamiliarisationTime ());
        }
      } else if (retrievedChunk.getImage().matches (newInformation)) {
        ListPattern toadd = retrievedChunk.getImage().clone ();
        toadd.setNotFinished ();
        _image = _image.append (toadd);
        model.advanceClock (model.getFamiliarisationTime ());
      } else { // could not retrieve a chunk, so look for, or learn a primitive
        ListPattern primitive = newInformation.getFirstItem ();
        retrievedChunk = model.recognise (primitive);
        if (retrievedChunk == model.getLtm ()) {
          return model.getLtm().learnPrimitive (model, primitive);
        } else {
          ListPattern toadd = primitive.clone ();
          toadd.setNotFinished ();
          _image = _image.append (toadd);
          model.advanceClock (model.getFamiliarisationTime ());
        }
      }
    }

    return this;
  }
}

