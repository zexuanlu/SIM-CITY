##SimCity v1 Team 36 Repository

###Team Information
  + [SimCity Team 36 Wiki](https://github.com/usc-csci201-fall2013/team36/wiki)

###Running the Program
  1.  To run the program, open Eclipse.
  2.  Select 'File', then 'Other', then 'New Java Project from Existing Ant Buildfile.'
  3.  Click the 'Next' button, and then select Browse. Find the build.xml file in whatever folder you cloned into. (default name is team36)
  4.  Hit Finish.
  5.  May need to include JUnit4 into libraries.
  5.  Go into the project folder, into src, into the gui.main package, and select SimCityGUI.java.
  6.  In the drop down menu for run, select run as, and click Java Application.
  7.  SimCity should run

###How to run the various scenarios
  A. Click "One not working person" and click run
  B. Click "Three not working people" and click run
  C. While running scenario A, right click and hold on restaurants, move over Empty Stock, and release
  D. Only for large teams - Not applicable
  E. Should be seen in scenario B.
  F. While running scenario A, right click and hold on buildings, move over close and release. The person will avoid the closed buildings.
  G. Same as scenario C, but close the restaurant before the delivery arrives
  H. Only for large teams - Not applicable
  I. Only for large teams - Not applicable
  J. Click Full 50-person scenario and click run
  K. Part of the Gui - seen in all scenarios 
  L. Part of the Gui - seen in all scenarios
  M. Seen in all scenarios
  N. Located on the wiki
  O. While running scenario A, click "Robber robs Bank" and click run
  P. Click "Vehicle Collision" and click run
  Q. Click "Person Vehicle Collision" and click run
  R. Click "Weekend Behavior" and click run
  S. Shift changes should be seen in scenario J, the 50-person scenario
  T. Seen in all the restaurants in scenario B - both types of waiters exist and work
  U. Seen on the wiki and in code
  V. See below with the team member contributions
###Team Members Contributions for V1 and V2
  1. Jennie Zhou - 
	+ Houses and Apartments
	+ Residents, Landlords, Apartment Tenants
	+ Person Interaction Panel
	+ Some of Add Person Panel
	+ Team Management
	+ Fixes of other code and integration
	+ Upgrade of restaurant6
	+ Worked on scenario panel
	+ Overall restaurant fixes for producer consumer
	+ Market delivery scenario
	+ GUI additions
  2. Lizhi Fan -
	+ Transportation
	+ Busses and Cars
	+ A* for Busses and Cars
	+ City Layout
	+ Person Initialization in Main Class
	+ Fixes of other code and integration
	+ Upgrade of restaurant5
	+ Vehicle-person collision
	+ Vehicle-vehicle collision
	+ Traffic light
  3. Joseph Boman - 
	+ Bank
	+ Clickable buildings in City View
	+ Time Card and Role Classes
	+ Git (Merging Issues, Commits, Everything else)
	+ Person Initialization in Main Class
	+ Fixes with Person and Person scheduler
	+ Individual Location GUIs
	+ Fixes of other code and integration
	+ Some of PersonAgent
	+ Upgrade of restaurant4
	+ GUI radial button upgrade upon right click
	+ Bank robbery scenario
	+ Market delivery scenario
	+ Open and close scenario
	+ Fixes and debugging
  4. Grant Collins -
	+ Person Agent
	+ Person Initialization in Main Class
	+ CityMap Class
	+ Global Clock and Locations
	+ Integration
	+ Upgrade of restaurant3
	+ Weekend scenario
	+ Upgraded restaurant 2
	+ Traffic light
	+ Fixes and debugging
  5. Rocky Luo -
	+ Market
	+ Market Trucks
	+ Integration
	+ Upgrade of restaurant2
	+ Traffic light
	+ Helped with images in CityAnimationPanel 
	+ Market delivery fail scenario
  6. Mikhail Bhuta -
	+ Person Interaction Panel
	+ Trace Panel
	+ All gui panel layout and formatting
	+ Integration
	+ Upgrade of restaurant 1 and 2
	+ Created images and integrated them for all agent and panel animations
	+ Some fixes and debugging
	+ Fixes to panel looks and formatting 

###Issues and Known Bugs 
  + People sometimes do not disappear into buildings
  + People sometimes do not take bus when they should
  + Only 1 upgraded restaurant included, however, A* was implemented
  + Clicking too fast on buildings makes them not show up
  + Homes are maintained 1 time per day and rent is paid 1 time per day
  + People do not have A*, and inside of buildings does not have A*
  + Configuration file not used, people initialized inside a class
  + Some commit messages are less than professional - these will be amended as soon as possible
  + Some tests were removed from Person due to the lack of a GUI in the tests
  + Because CarAgent is so integrated with A* and it's GUI it was unable to be JUNIT tested
  + For Transportation, focused more on A* and GUI than on JUNIT testing
  + Paths to gui icon images may be currupt on pull, copy images from src folder to respective bin folders (i.e. 		    src/restaurant1/gui -> bin/restaurant1/gui)
  + Null pointer exceptions in the 50-person scenario. This is due to the fact that the second shift people do not handle their locations appropriately, even when given a home. The shift changes can be properly identified as working when SimCityGUI.java is uncommented. Sorry for the inconvenience.
