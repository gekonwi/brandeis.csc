group_assigner
=============

The initial application of this algorithm was targeted at Brandeis University in Fall 2014. In the class CS118a Computer Supported Collaboration, taught by Prof. Richard Alterman, we needed to assign students to groups with specific conditions.

During the first round students within one group were suppposed to sit next to each other. During the second round students within one group were supposed to sit as far as possible from each other.

For the first round the students were guided to a Google Spreadhseet where they assigned themselves together with their neighbor to a group respectively. Each group was asked to enter their "class coordinates" (row and seat number).

The algorithm takes in all this information and generates the group assignment for the second round. The input format is the result of copying the content of the Google Spreadsheet. The output format allows copy-paste into a Google Spreadsheet with correct distribution on cells.
