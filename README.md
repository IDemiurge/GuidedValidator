<h1> Coding exercise in OOP </h1>

<h2> Validator class </h2>
Since status text and label colors logic are both the product of our validation, this class assumes both of these responsibilities 
at this stage. If the logic becomes more complex, it is to be refactored.

<h2> ValidatorDataSource </h2>

To test that application works correctly with other (possibly slower) sources of valid combinations, 
I have added <i>TestValidatorDataSource</i> that imitates a blocking IO operation with <i>sleep()</i>. 
You can see that application is not freezing but just waits for the operation to complete, 
then updates UI for whatever selection was done while it was loading.

<h2> Potential improvements </h2>
When there are no valid options in a column in which an item is selected 
(and we actually need to deselect to get a valid combination), 
we could do more than highlight text in red - e.g. colorize entire section's background into red or display a 
tip in status label (which would be easy with <i>CombinationValidator::getValidationText</i>)
For this, a separate class e.g. <i>Colorizer</i> could be added to encapsulate a more nuanced behavior