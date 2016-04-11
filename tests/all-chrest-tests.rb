# Ruby test suite for Chrest
# Assumes base of compiled Chrest code is on the CLASSPATH
# e.g. jruby -J-cp bin all-chrest-tests.rb

require "java" 
require "modellers_testing_framework"
require "securerandom"
include TestFramework

# Import all required Java classes
import "java.awt.Color"
import "java.util.ArrayList"
import "java.util.HashMap"
import "java.util.TreeMap"

# Import all CHREST package classes.
[
  "Chrest", 
  "Link",
  "Node",
  "Perceiver",
  "Stm",
  "VisualSpatialField"
].each do |klass|
  import "jchrest.architecture.#{klass}"
end

[
  "DomainSpecifics",
  "Fixation",
  "FixationResult",
  "Scene",
  "SceneObject"
].each do |klass|
  import "jchrest.domainSpecifics.#{klass}"
end

[
  "ChessBoard",
  "ChessDomain",
  "ChessObject"
].each do |klass|
  import "jchrest.domainSpecifics.chess.#{klass}"
end

[
  "AttackDefenseFixation",
  "GlobalStrategyFixation",
  "SalientManFixation"
].each do |klass|
  import "jchrest.domainSpecifics.chess.fixations.#{klass}"
end

[
  "CentralFixation",
  "HypothesisDiscriminationFixation",
  "PeripheralItemFixation",
  "PeripheralSquareFixation"
].each do |klass|
  import "jchrest.domainSpecifics.fixations.#{klass}"
end

[
  "GenericDomain"
].each do |klass|
  import "jchrest.domainSpecifics.generic.#{klass}"
end

[
  "ItemSquarePattern",
  "ListPattern",
  "Modality",
  "HistoryTreeMap",
  "VisualSpatialFieldMoveObjectException",
  "NumberPattern",
  "Pattern",
  "ReinforcementLearning",
  "Square",
  "StringPattern",
  "VisualSpatialFieldObject"
].each do |klass|
  import "jchrest.lib.#{klass}"
end

# Pick up all ruby test files except this one
Dir.glob(File.dirname(__FILE__) + "/**/*.rb") do |file|
  require file unless 
  File.expand_path(file) == File.expand_path(__FILE__) ||
    file.to_s.end_with?("tileworld-domain-tests.rb")
end

puts "Testing CHREST:"
TestFramework.run_all_tests
