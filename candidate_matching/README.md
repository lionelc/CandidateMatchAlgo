I use Eclipse for writing and testing the code. 

The required input is from the challenge: reqs.csv and talent-profiles.csv

Usage:

The executable class is candidate_matching.Main . Also, you can compile it as a jar file via command line (a simple "ant build-jar" under the directory would work).

Then you can run the program as follows:

```
java -cp dist/candidate-matching-lionelc.jar candidate_matching.Main -r reqs.csv -t talent-profiles.csv -q AAAm4xAAqAAALJbAA
```

The output is basically the ranking (descendent) of all the candidates for this given job requisition, like


>Evaluating candidate AAAm7XAAqAAALz/AAC : 2.3

>Evaluating candidate AAAm7XAAqAAALz/AAD : 0.0

>Evaluating candidate AAAm7XAAqAAALz/AAE : 0.0

>AAAm7XAAqAAALoeAAA 2.9

>AAAm7XAAqAAALogAAC 2.9

>AAAm7XAAqAAALomAAC 2.9

>AAAm7XAAqAAALonAAA 2.9


Note:

1. I considered using formal/general Natural Language Processing packages for help, and then realize the benefit would be very limited while it might end up with using an over-complicated process for little effects. NLP could help in dealing with word stemming, stop words and such low-level stuff. However, in terms of digging out the meaning of job description/candidate profile, it is more about **domain-specific terms**. e.g. A general part-of-speech would look too burdensome for this application. 

Thus, the code uses some simple methods for natural language processing with human-inputted domain knowledge. For a more formal product implementation, that part should be a rigorous set of words. 

+ stopwords.txt : standard English stopwords. Used for filtering out non-job-essense-related words.
+ majorwords.txt : some words denoting the majors in college. It help with understanding degree requirements.
+ commonwords.txt : some common words that we don't consider as job skills, but just common words for connecting the contexts.
+ synonyms.txt : some job-skill-related words representing equivalent skills. In one line, such synonyms are separated by comma.

2.
 The input candidate file is not well formatted for all the lines. So I got some warning in parsing. But the result here is merely used for **Proof of Concept** instead of product implementation.  



