# Rationale:
A typical problem that teams doing CI (continuous integration) try and solve is to get the build time to an acceptable amount so that frequent checkins are possible. However, with a steady increase in the number of tests, the time taken to run these tests on every checkin keeps increasing. Solving this problem in the build is almost always non trivial. This is where parallelizing builds comes handy. Throwing hardware at this problem is one of the potential solutions to get build time under acceptable limits.

**TestLoadBalancer (TLB)** aims at splitting your entire test suite into mutually exclusive units such that each of the unit can be executed in parallel. Assuming that tests are written independent of each other, which is a best practice in writing tests, the tests can be ordered and arranged in anyway and *TLB* leverages this fact in order to split the test suite and reorder the tests.

# Documentation:
Detailed documentation of TLB concepts and configuration options is available in the [TLB wiki](http://wiki.github.com/janmejay/tlb).

# Supported Frameworks:
Currently, *TLB* supports:
 * JUnit using Ant & Buildr
 * [Twist™](http://www.thoughtworks-studios.com/agile-test-automation "ThoughtWorks Studios - Twist") using Ant & Buildr
 * Experimental support for RSpec using Rake. Checkout the ["tlb_rb"](http://github.com/janmejay/tlb_rb)

# Adding support for a new frameworks:
## Testing framework
*TLB* assumes that a test framework provides an option to specify a list of file resources that get executed. The initial list is passed to the criteria chain. Splitter criterion prunes the file resource list. After this the list of the file resources is passed through the orderer, where it gets reordered. The contract is that the orderer does not change the number of file resources.

The final list of file resources is what is fed into the test framework for execution.

Once the tests are executed, *TLB* needs a way to capture the test result and the time a test took to execute in order to report to the *TLB Server*. Without this, the orderer and the TimeBasedCriteria will not work.

## Build tool
Supporting other build tools is a matter of implementing the end user interface which delegate to the Splitter and Orderer.

# Contributors:
### Core Team:
  * Pavan K Sudarshan [http://github.com/itspanzi](http://github.com/itspanzi "Github Page")
  * Janmejay Singh [http://codehunk.wordpress.com](http://codehunk.wordpress.com "Blog")

### Other Contributors:
  * Chris Turner [http://github.com/BestFriendChris](http://github.com/BestFriendChris "Github Page")

# History:
  *TLB* started in Jan 2010 as an attempt to enhance another similar project called TestLoadBalancer(hosted on code.google.com and github.com). However due to some other issues inheriting the codebase for reuse/enhancement was not possible, besides the direction planned for the new codebase was not aligned with the structure of TestLoadBalancer codebase, so we decided to build *TLB* from scratch. The old TestLoadBalancer originally implemented the idea of load balancing tests based on count, and inspired the creation of *TLB*. The project is not developed or maintained anymore and is not hosted publicly. 

### People behind the TestLoadBalancer(the old project)
  * Li Yanhui [http://whimet.blogspot.com](http://whimet.blogspot.com "Blog")
  * Hu Kai [http://iamhukai.com](http://iamhukai.com "Blog")
  * Derek Yang [http://dyang.github.com/](http://dyang.github.com/ "Github Page")
