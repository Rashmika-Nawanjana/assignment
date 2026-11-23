Technical Training – Requirement for software
Sun Travels is a travel agency which handles accommodation bookings on behalf of their customers.
They get paper contracts from hoteliers with room types, number of rooms and rates. Sun Travels
keeps a markup (15% at the moment) and sells those hotel rooms to travelers. Sun Travels has its
own call center, in which all the rate calculations are done looking at paper contracts.
Sun Travels is now in the need of software to automate the above mentioned process. I.e. They want
to implement a software system to load paper based contracts into the system and define their
markups. In addition they expect the system to provide a user interface for call center agents to
search contracts in the system. That search should handle all the calculations and show the call
center agent how much the customer should be charged for his/her booking.
Following is a sample paper contract
Hilton Colombo
==========
This contract is valid from 2012-01-01 to 2012-12-31
Room Types Price No of rooms Max Adults
Standard $ 35.00 15 2
Sea View $ 55.00 8 3
All above rates are per person, per night
Following should be available in the new system user interface for reservation agent

An interface to enter customer's search criteria
1. Check in date
2. No of nights
3. How many rooms required, with no of adults coming e.g.
- 1 room with 2 adults
- 1 room with 2 adults and 1 room with 1 adult
Upon submitting the above, the system should present the results with pricing. In that screen,

following should be available at least
1. Hotel name
2. Room Type
3. Price (Marked up)
4. Room availability status (I.e. if no of rooms available are enough to accommodate the
request (Available) or not (Not available))
Rules
● Customers can stay only within contract valid period
● If all the rooms requested by the customer can be accommodated, we call that room type as
Available. Otherwise, Unavailable.
● Price shown to customer = price given by hotelier for the room type*markup*number of
nights*number of adults

Notes
● You are expected to use spring boot framework with java to develop RESTful web service
backend.
● It should have at least two separate user interfaces to Load/View contracts into/from the
system and the other for call center agents to enter search criteria and view results. You can
add any number of other UIs as you need.
● It’s required to use Angular framework to implement Load/View contracts interfaces and
searching interfaces.
● It is preferred to have a user interface to search contracts also.
● You must adhere to coding practices
● At any given point (Data loading, data saving and searching), your user interface should not be
unresponsive

Assignment Submissions and Evaluation
start of the assigned time period for the assignment.
● You are required to design and submit an ER Diagram of the program within 3 days from the
start of the assigned time period for the assignment.
● You are required to write and submit user stories based on the requirement and your solution
within 4 days from the start of the assigned time period for the assignment. These user
stories will be reviewed by panel members, Final user stories need to be submitted at the
submission deadline.
● You are required to write and submit test cases and test plan based on the requirement and
your solution within 5 days from the start of the assigned time period for the assignment.
These test cases will be reviewed by panel members. Final test cases and plan required to be
submitted at the submission deadline.
● You are required to complete development of your software solution within 10 days from
the start of the assigned time period for the assignment.
● You will be provided a GitLab versioning space to manage your source code. You are required
to commit your source code daily.
● At the end of the assigned development period, you are required to submit following

○ Source code of the system need to be committed to the given gitlab repository on or
before deadline ( final commit before deadline will be considered )
○ Finalized user stories ( based on review comments )
○ Finalized test cases and test plan
○ User Manual for the system
● You are required to demonstrate your software solution in front of a panel of experts and
defend your solution in a viva session. ( both application UI and source code will be
evaluated )
● You are required to be present when the colleague whose system you have tested will be
presenting his/ her solution to the panel and you will be evaluated based on the testing you
have done and your findings.