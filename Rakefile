# Rake file for managing the Chrest project

JRUBY = 'jruby-1.6.7.2' # name of jruby executable

directory 'bin'

desc 'compile Chrest classes into bin folder'
task :compile => 'bin' do
  Dir.chdir ('src/jchrest-architecture') do
    sh 'javac -cp ../../lib/jcommon-1.0.16.jar:../../lib/jfreechart-1.0.13.jar -d ../../bin `find -name "*.java"`'
  end
end

desc 'run Chrest, from compiled code'
task :run => :compile do
  sh 'java -cp bin:lib/jcommon-1.0.16.jar:lib/jfreechart-1.0.13.jar jchrest/gui/Shell'
end

directory 'tmp'

desc 'extract java libs into tmp directory'
task :extract_libs => 'tmp' do
  Dir.chdir('tmp') do
    sh 'jar -xf ../lib/jcommon-1.0.16.jar'
    sh 'jar -xf ../lib/jfreechart-1.0.13.jar'
  end
end

desc 'build Chrest into a self-contained jar file'
task :make_jar => [:compile, :extract_libs] do 
  sh 'jar -cfm chrest.jar lib/Manifest lib/orange-chrest-logo.png -C bin . -C tmp .'
end

desc 'remove the bin/release/tmp directories'
task :clean do
  sh 'rm -rf bin'
  sh 'rm -rf tmp'
  sh 'rm -rf release'
end

desc 'build the user guide'
task :guide do
  Dir.chdir('doc/user-guide') do
    sh 'latex user-guide'
    sh 'latex user-guide'
    sh 'latex user-guide'
    sh 'dvipdf user-guide.dvi'
  end
end

desc 'show the user guide'
task :show_guide => :guide do
  Dir.chdir('doc/user-guide') do
    sh 'evince user-guide.pdf'
  end
end

directory 'doc/api'
desc 'create API documentation'
task :api_doc => 'doc/api' do
  Dir.chdir('src/jchrest-architecture') do
    sh 'javadoc -classpath ../../lib/jcommon-1.0.16.jar:../../lib/jfreechart-1.0.13.jar -d ../../doc/api `find -name "*.java"`'
  end
end

directory 'release/chrest'
desc 'bundle for release'
task :bundle => [:guide, :make_jar, :api_doc, 'release/chrest'] do
  Dir.chdir('release/chrest') do
    sh 'cp ../../chrest.jar .'
    sh 'cp -r ../../examples .'
    sh 'cp ../../doc/user-guide/user-guide.pdf .'
    sh 'cp -r ../../doc/api ./javadoc'
    sh 'rm examples/ruby/chrest.jar'
  end
  Dir.chdir('release') do
    sh 'zip -r chrest-1.0.0.zip chrest'
  end
end

desc 'run all Chrest tests'
task :test => :compile do
  Dir.chdir('src/tests') do
    sh "#{JRUBY} --1.9 -J-cp ../../bin all-chrest-tests.rb"
  end
end