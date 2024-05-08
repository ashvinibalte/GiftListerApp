<h1><b1>Project Description</b1></h1>
<h2><b1>GiftListerApp</b1></h2> is a mobile application designed to facilitate the management and pledging of gift lists. This application enables users to create, view, and pledge for gift lists in a user-friendly environment, leveraging the real-time capabilities of Firebase Firestore coupled with secure Firebase Authentication for user management.

1. The app allows a user to create a gift list and solicit pledges from other users to
purchase items in their posted gift lists.
2. You are provided with a skeleton app the includes all the UI implementation and the
Authentication flows.
3. The gift lists should be stored in a collection called “giftlists” where each gift list
should be stored as a separate document under the “giftlists” collection.
4. Each the gift list document should include a “giftlist-products” sub-collection which
should store each gift list product as a separate document under this sub-collection.

![12](https://github.com/ashvinibalte/GiftListerApp/assets/125997432/16666763-e022-4b6f-9cb9-0f3ba1949884)


<h2><b1>Part 1, Gift Lists</b1></h2>

This screen displays a list of gift lists as shown in Figure 1(a). Please follow the steps:
1. Setup a snapshot listener to retrieve the “giftlists” collection and display the gift lists as shown in Fig 1(a).
a. If needed disable the snapshot listener when this screen UI is destroyed.
2. Each gift list row item should display gift list name, gift list creator’s name, total number items, the total pledged amount compared to the total amount and a comma separated list of the tags.
a. The label displaying the progress should show pledge amount out of total amount.
b. The progress bar displays the percentage pledged amount compared to the total amount.
c. Tags should be stored as an array in the gift list document.
3. Clicking on a row item should transition to the Gift List screen, send it the documentId
of the selected GiftList item.
4. Clicking on “Filter” transitions to the Filter Screen.
5. Clicking on “Add New” transitions to the Create Gift List.

<h2><b1> Part 2, Create Gift List Screen</b1></h2>

This screen is a form to enable the user to create new gift list. This screen displays a list
of products that will can be added to the new gift list, please follow the steps:
1. The list of available products is retrieved from the products API and is displayed as
shown in Figure 1(b).
1. The “+” button indicates the product is not added, and the “-” button indicates the
product has been added.
2. Clicking on the “+” icon should:
a. Selects the product to be added to the gift list.
3. Clicking on the “-” icon should:
a. Removes the product from the selected gift list products.
4. Clicking on the “Select” button allows the user to select the tags to associate with
this gift list. The tags screen returns a list of tags that should be associated with the
gift list and is displayed in Figure 1(d).
5. Clicking on the Submit button should:
a. If the name is not entered or no tags selected or no products selected then show
an alert dialog or toast message indicating the missing form input.
b. If all the required data is entered correctly then:
- Store the new gift list document in the “giftlists” collection. The tags should be
stored as an array of strings in the gift list document.
- Store the gift list product documents in the “giftlist-products” sub-collection
under the newly created gift list document.
- Upon successful completion go back to the previous screen as shown in
Figure 2(a).

![21](https://github.com/ashvinibalte/GiftListerApp/assets/125997432/98ee7668-74aa-443c-987a-d5dd19996e96)

<h2><b1>Part 3, Gift List Screen</b1></h2>

This screen display the details of specific Gift List as shown in Figure 2(b). Please follow
the steps:
1. This screen receives the Gift List documentId from the previous screen, it does not
receive the gift list object instead you should retrieve it using a snapshot listener.
2. Setup a snapshot listener to retrieve the gift list document and display gift list details
shown in Figure 2(a).
a. The displayed gift list information should be updated as the snapshot listener
receives updates.
b. If needed you should disable the snapshot listener when this screen is destroyed.
3. Setup another snapshot listener to retrieve the “giftlist-products” sub-collection for the
gift list document and display the gift list products as shown in Fig 2(b).
a. The displayed gift list product information should be updated as the snapshot
listener receives updates.
b. If needed you should disable the snapshot listener when this screen is destroyed.
c. Note that each gift list product is either pledged or not pledged, you should
consider storing the required attributes to track this in the gift list product
document on firestore.
4. The “filled check icon” indicates that this gift list product has been pledged by a user
in the system. The name of the user should be displayed as shown in Figure 2(c).
5. The “not filled check icon” indicates that this gift list product has not been pledged by
a user in the system. Not pledged should be displayed as shown in Figure 2(c).
6. A user should not be allowed to pledge or un-pledge any of the gift list
products in a gift list they have created.
7. Clicking on the “not filled check icon” icon should:
a. Update gift list product document to indicate that the product has been pledged.
b. Update the gift list document to update any required attributes.
c. The snapshot listeners will be triggered automatically upon these updates which
will refresh the table and the displayed gift list details.
8. Clicking on the “filled check icon” icon should:
a. Only current pledged user is the user who is able to remove their original pledge.
b. Update the gift list product document to indicate that the product is not pledged by
a user in the system. (Which is un-pledge operation).
c. Update the gift list document to update any required attributes.
d. The snapshot listeners will be triggered automatically upon these updates which
will refresh the table and the displayed gift list details.

<h2><b1>Part 4, Filter Screen</b1></h2>

This screen allows the user to filter the Gift Lists displayed based on the array of
selected tags as shown in Figure 3(b). Please follow the steps:
1. Using the Tags screen the user is able to select the array of tags that is sent to this
screen to filter the gift lists by the provided array of tags.
2. Upon selecting tags:
a. Retrieve the list of gift list documents that contain any of the selected tags (tag1 or
tag2 or … ). Filtering should be done on Firestore by sending the selected query,
do not do the filtering on the client side. To locate the appropriate query Please
check https://firebase.google.com/docs/firestore/query-data/queries
b. Refresh the displayed list to display the results returned by Firestore.
3. Upon clearing the tags:
a. Retrieve all list of gift list documents from Firestone, no query required.
b. Refresh the displayed list to display the results returned by Firestore.

![22](https://github.com/ashvinibalte/GiftListerApp/assets/125997432/6b3f59f8-e27d-4b65-8b81-681138c16b9e)

