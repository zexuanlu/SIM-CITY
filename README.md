##SimCity v1 Team 36 Repository

###Team Information
  + [SimCity Team 36 Wiki](https://github.com/usc-csci201-fall2013/team36/wiki)

###Running the Program
  1.  To run the program, open Eclipse.
  2.  Select 'File', then 'Other', then 'New Java Project from Existing Ant Buildfile.'
  3.  Click the 'Next' button, and then select Browse. Find the build.xml file in whatever folder you cloned into. (default name is team36)
  4.  Hit Finish.
  5.  Go into the project folder, into src, into the gui.main package, and select SimCityGUI.java.
  6.  In the drop down menu for run, select run as, and click Java Application.
  7.  SimCity should run

###What you will see when you run the code
####City and Animation Mechanics
  1. A complex bus system involving A* and multiple bus stops (Yellow)
  2. Cars, which also function via A* (Red)
  3. Buildings which display name upon mouseover and are clickable to display interior
  4. Ability to add roles to people via the interaction panel (note: due to person AI, it may take some time before the requested role occurs)
####Interactions and Behavior
  1. Various people are instantiated at various locations.
  2. Some of those people will be rich people - they have cars and don't work.
  3. Other people will go to work.
  4. People will take the bus or walk, depending on their location and their destination.
  5. If people have lots of money, as they do upon instantiation, they will go to the bank and deposit some.
  6. If people are low on money, they will go to the bank and withdraw some.
  7. People go to the restaurant or eat at home when they get hungry.
  8. People go to the market when they have no food at home and are poor
  9. People who have nothing to do go to their home or apartment
  10. People leave work when their replacements arrive or at the end of the day

###Team Members Contributions
  1. Jennie Zhou - 
	+ Houses and Apartments
	+ Residents, Landlords, Apartment Tenants
	+ Person Interaction Panel
	+ Team Management
	+ Fixes of other code and integration
  2. Lizhi Fan -
	+ Transportation
	+ Busses and Cars
	+ A* for Busses and Cars
	+ City Layout
	+ Person Initialization in Main Class
	+ Fixes of other code and integration
  3. Joseph Boman - 
	+ Bank
	+ Clickable buildings in City View
	+ Time Card and Role Classes
	+ Git (Merging Issues, Commits, Everything else)
	+ Person Initialization in Main Class
	+ Fixes with Person and Person scheduler
	+ Individual Location GUIs
	+ Fixes of other code and integration
  4. Grant Collins -
	+ Person Agent
	+ Person Initialization in Main Class
	+ CityMap Class
	+ Global Clock and Locations
	+ Integration
  5. Rocky Luo -
	+ Market
	+ Market Trucks
	+ Integration
  6. Mikhail Bhuta -
	+ Restaurant Upgrades
	+ Since he did the restaurant and gui, he only has one interaction diagram and doc
	+ Person Interaction Panel
	+ Trace Panel
	+ Overall gui layout
	+ Integration 