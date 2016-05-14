In my test environment, I use Eclipse to build the project and run the tests there. The original code is from https://github.com/liuanan/gbdt . 

In the executable Java class test.GBDTTest, it runs over the commands from util.txt with valid results.

The input is a feature-based file (same for training and test data set):

label feature_order_1:feature_value_1 feature_order_2:feature_value_2 ...

like 

1 0:26503 1:511405 2:511405 3:500000 4:366 5:19.2954
1 0:6466 1:520845 2:520845 3:500000 4:638 5:80.5389
1 0:181697 1:661807 2:519048 3:166479 4:104 5:2.85665
0 0:116354 1:528894 2:528894 3:500000 4:1445 5:4.54552
1 0:9476 1:548764 2:548764 3:500000 4:842 5:57.9048
0 0:73888 1:656041 2:320480 3:99013 4:3022 5:4.33732
1 0:31490 1:529156 2:547408 3:57773 4:3759 5:17.383
0 0:33855 1:658431 2:448677 3:110697 4:31650 5:13.2525
0 0:154108 1:438876 2:399905 3:47425 4:168727 5:2.59495
1 0:5496 1:481974 2:481974 3:500000 4:432 5:87.6795 

The training takes a minute for the given data (1500 data record for training, 500 for test). Then it outputs the test results like:

gold	predict

1.0	0.8240058029079873

1.0	0.7014812912873722

0.0	0.3718339286288691

0.0	0.36788644001641546

0.0	-0.5750447320094734

0.0	0.5007040415952885

0.0	0.13588174211488638

0.0	-0.19791820512855637

1.0	0.8062036028892376

0.0	0.48544759487452616

0.0	0.6058251427251529

0.0	-0.15945158686959174

1.0	0.766367906958527

1.0	0.7467008969271283

0.0	0.017215162776922102

0.0	-0.011831075034606162

In **candidate_matching** folder, there's a method toFeatureString in class candidate_matching.Candidate, which converts the Candidate instance to a GBDT compatible format as input data. It would be helpful for assembly.
