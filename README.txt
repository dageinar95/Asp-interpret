This is the Asp intepret I wrote in the autumn/winter of 2020. It is a program that reads and analyzes
a program in the Asp language (Asp is based on Python syntax), and then executes what the program code entails.

It is divided into 3 parts:
1. The scanner reads code from a file og divides it into tokens, symbol by symbol. 
2. The parser checks that the code is correct and follows the definitions and rules of the language (syntax).
3. The evaluator actually executes the code and evaluates all the expressions.

In the "no" directory you will find all .java-files with code. 

To compile all files, use "ant" (requires Apache Ant) while in the main directory. 

To run an Asp file with actual Asp code, use: java -jar asp.jar <asp_file_name.asp>