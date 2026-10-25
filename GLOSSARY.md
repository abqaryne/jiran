# Jiran

Jiran is an Android app for UM students who live off campus. It joins finding a room, finding housemates, getting to campus and running a shared household in one place.

## Language

### Housing

**Unit**:
The physical flat or house that is rented, with one or more rooms and one tenancy agreement.
_Avoid_: Property, apartment, flat, house, home

**Building**:
The named block or development a Unit belongs to (for example Residensi Pantai Dalam). Reviews and the area page attach to a Building.
_Avoid_: Property name, condo, complex

**Room**:
One lockable bedroom in a Unit, of type master, medium or small. A Room is what a student actually rents.
_Avoid_: Bedroom, space, bed

**Room price**:
The rent a Room's occupant pays each month. The Room prices of a Unit never add up to more than the Unit rent.
_Avoid_: Rent (alone), room rate

**Unit rent**:
The total monthly rent the Host pays the landlord for the whole Unit.
_Avoid_: Total rent, base rent

**Listing**:
A public announcement for one Unit that offers one or more vacant Rooms. A student sees it as one **Room card** per vacant Room.
_Avoid_: Ad, post (as a noun), property listing

**Room card**:
The search-result tile for one vacant Room, showing its price, travel time and true monthly cost.
_Avoid_: Listing card

**Host**:
The verified UM student who holds, or is about to hold, the tenancy of a Unit and posts its Listing. There is at most one active Host per Unit. Landlords are not Hosts and have no account.
_Avoid_: Landlord, owner, poster, agent

**Evaluation**:
A private what-if for a Unit found on another site, entered by a Group or a student to see room prices, monthly cost and commute. It is never shown to others and is not a Listing.
_Avoid_: Draft listing, saved listing

### People and relationships

**Match**:
A suggestion from the system of someone who suits you, shown with a score and reasons. A Match is not yet a relationship.
_Avoid_: Connection, pair, roommate request

**Match score**:
A percentage worked out by a fixed formula from two people's answers. The AI never sets it.
_Avoid_: Rating, compatibility percentage

**Connection request**:
One side's request to talk, sent to a Match or to a Host.
_Avoid_: Invite, friend request, message request

**Connection**:
The state where both sides have accepted. Only a Connection opens chat and contact details.
_Avoid_: Match, contact, friendship

**Group**:
Two to four people who plan to rent a whole Unit together and agree on a Room price split before anyone signs. It ends when the tenancy starts.
_Avoid_: Team, party, roommates

**Household**:
The people who currently live in a Unit. A Group becomes the Household when the tenancy starts, and a new member joins only after the Host confirms their move-in.
_Avoid_: House, flat-mates (as a group), residents

**Housemate**:
A member of a Household, seen from another member.
_Avoid_: Roommate, flatmate

### Money and commuting

**True monthly cost**:
A Room price plus the person's equal share of utilities plus the commute cost of the cheapest travel mode that still reaches class on time at their real departure time. If no mode is on time, no figure is shown.
_Avoid_: Total cost, real cost, all-in cost

**Commute option**:
One way of getting from a Room to the student's faculty (walk, bus or LRT plus shuttle, e-hailing, drive alone), with its time, monthly cost and CO₂ estimate.
_Avoid_: Route, transport mode

**Peak and off-peak**:
The two times of day for which each Commute option is shown, because road time and e-hailing price change between them.
_Avoid_: Rush hour, busy time
