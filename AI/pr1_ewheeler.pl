%Symptoms, and the questions corresponding to them.
question('Severe bleeding', 'Are they bleeding severely?').
question('Fracture', 'Are there any broken bones?').
question('Burn', 'Are there burns on their skin?').
question('Snakebite', 'Have they been bitten by a snake?').
question('Animal bite', 'Have they been bitten by an animal?').
question('Puncture wound','Have they suffered a puncture wound?').
question('Heat exposure', 'Have they been in a hot environment for an extended period?').
question('Cold exposure', 'Have they been in a cold environment for an extended period?').
question('Head trauma', 'Have they suffered a heavy blow to the head?').
question('Problems breathing', 'Are they having problems breathing?').
question('Choking', 'Are they incapable of speaking?').
question('Rapid breathing','Is their breathing rapid, do they have a rapid pulse or are they short of breath?').
question('Slow breathing','Is their breathing slow?').
question('Fatigue', 'Are they fatigued?').
question('Confusion', 'Are they confused?').
question('Shock', 'Are they in shock?').
question('Lightheadedness', 'Are they feeling lightheaded?').
question('Dizziness', 'Are they dizzy or disoriented?').
question('Fever', 'Do they have a fever?').
question('Nausea', 'Are they nauseous?').
question('Swelling', 'Is there swelling, externally or internally?').
question('Headache', 'Do they have a headache?').
question('Fainting', 'Have they or are they fainting?').
question('Cool skin', 'Is their skin cool to the touch?').
question('Itching, burning or numbness', 'Is their skin irritated?').
question('Lasting chest pressure or pain', 'Is there uncomfortable pressure or pain in the chest (and possibly shoulders, neck or arms) lasting more than a few minutes?').
question('Cardiac arrest, irregular heart rhythm or respiratory failure','Are they in cardiac arrest, have an irregular heart rhythm or have respiratory failure?').
question('Sweating','Are they sweating?').
question('Shivering','Are they shivering?').
question('Seizures','Are they having a seizure?').

%Diagnoses.
diagnosis('Allergic Reaction').
diagnosis('Animal Bite').
diagnosis('Burn').
diagnosis('Electric Shock').
diagnosis('Fracture').
diagnosis('Frostbite').
diagnosis('Head Trauma').
diagnosis('Heart Attack').
diagnosis('Heat Exhaustion/Stroke').
diagnosis('Hypothermia').
diagnosis('Puncture Wound').
diagnosis('Severe Bleeding').
diagnosis('Snakebite').

%Read isSymptomOf(X, Y) as X is a symptom of Y.
isSymptomOf('Shock', 'Allergic Reaction').
isSymptomOf('Problems breathing', 'Allergic Reaction').
isSymptomOf('Swelling', 'Allergic Reaction').
isSymptomOf('Nausea', 'Allergic Reaction').
isSymptomOf('Dizziness', 'Allergic Reaction').
isSymptomOf('Fainting', 'Allergic Reaction').

isSymptomOf('Animal bite', 'Animal Bite').

isSymptomOf('Burn', 'Burn').

isSymptomOf('Cardiac arrest, irregular heart rhythm or respiratory failure','Electric Shock').
isSymptomOf('Burns','Electric Shock').
isSymptomOf('Seizures','Electric Shock').
isSymptomOf('Itching, tingling or numbness', 'Electric Shock').

isSymptomOf('Fracture', 'Fracture').


confidence('Frostbite', 0) :- exhibitsSymptom('Heat exposure'), !.
isSymptomOf('Cool skin', 'Frostbite').
isSymptomOf('Cold exposure', 'Frostbite').
isSymptomOf('Itching, burning or numbness', 'Frostbite').
%TODO: review symptoms

confidence('Head Trauma', 1) :- exhibitsSymptom('Head Trauma'), !.
isSymptomOf('Severe bleeding', 'Head Trauma'). 
isSymptomOf('Confusion', 'Head Trauma'). 
isSymptomOf('Dizziness', 'Head Trauma').
isSymptomOf('Headache', 'Head Trauma'). 
isSymptomOf('Fainting', 'Head Trauma'). 

confidence('Heart Attack', 1) :- exhibitsSymptom('Lasting chest pressure or pain'), !. 
isSymptomOf('Lightheadedness', 'Heart Attack').
isSymptomOf('Fainting', 'Heart Attack').
isSymptomOf('Sweating', 'Heart Attack').
isSymptomOf('Nausea', 'Heart Attack').
isSymptomOf('Rapid breathing', 'Heart Attack').

confidence('Heat Exhaustion/Stroke', 0) :- exhibitsSymptom('Cold exposure'), !.
isSymptomOf('Heat exposure', 'Heat Exhaustion/Stroke').
isSymptomOf('Rapid breathing', 'Heat Exhaustion/Stroke').
isSymptomOf('Dizziness', 'Heat Exhaustion/Stroke').
isSymptomOf('Headache', 'Heat Exhaustion/Stroke').
isSymptomOf('Sweating', 'Heat Exhaustion/Stroke').
isSymptomOf('Nausea', 'Heat Exhaustion/Stroke').
isSymptomOf('Fainting', 'Heat Exhaustion/Stroke').
isSymptomOf('Fever', 'Heat Exhaustion/Stroke').
isSymptomOf('Cool skin', 'Heat Exhaustion/Stroke').
isSymptomOf('Fatigue', 'Heat Exhaustion/Stroke').

isSymptomOf('Choking', 'Choking').

confidence('Hypothermia', 0) :- exhibitsSymptom('Heat exposure').
isSymptomOf('Shivering','Hypothermia').
isSymptomOf('Slow breathing','Hypothermia').
isSymptomOf('Cool skin','Hypothermia').
isSymptomOf('Dizziness','Hypothermia').
isSymptomOf('Fatigue','Hypothermia').
isSymptomOf('Confusion','Hypothermia').
isSymptomOf('Cold exposure','Hypothermia').

isSymptomOf('Puncture wound', 'Puncture Wound').

isSymptomOf('Severe bleeding', 'Severe Bleeding').

isSymptomOf('Snakebite', 'Snakebite').


treatment('Allergic Reaction','Call 911 immediately.').
treatment('Allergic Reaction','Check for medication').
treatment('Allergic Reaction','Lie them on their back.').
treatment('Allergic Reaction','Loosen tight clothing and cover them with a blanket. Don\'t give the person anything to drink.').
treatment('Allergic Reaction','If there\'s vomiting or bleeding from the mouth, turn the person on his or her side to prevent choking.').
treatment('Allergic Reaction','If there are no signs of breathing, coughing or movement, begin CPR. Do uninterrupted chest presses — about 100 every minute — until paramedics arrive.').

treatment('Animal Bite', 'If the wound is minor: wash the wound, apply antibiotic and cover the bite.').
treatment('Animal Bite', 'If the wound is deep: apply pressure, stop bleeding and call a doctor.').
treatment('Animal Bite', 'If there are signs of infection (swelling, redness, pain, oozing): see doctor immediately.').
treatment('Animal Bite', 'If you suspect the animal was rabid: see doctor immediately.').

treatment('Burn', 'If burn is small and not severe: Cool the burn bu holding it or immersing it in cool water, cover with sterile gauze, and take pain reliever.').
treatment('Burn', 'If burn is severe (muscle, fat or bone affected, charred or dry & white areas) or large: call 911, leave burned clothing on, check for circulation (perform CPR if there is none), elevate body part above heart, and cover burn with bandage, or clean, moist cloth.').

treatment('Choking', 'Perform Heimlich Maneuver. Stand behind the person (if not self), wrap arms around the waist and tip them forward; make a fist and position it above their navel; grasp the fist with the other hand and press quick and hard, upward into their abdomen.').
treatment('Choking', 'If performing on self: place fist slightly above navel, grasp fist with other hand, bend of hard surface, and shove fist inward and upward.').
treatment('Choking', 'Clear airway: lower on back to floor, perform CPR until object is in back or throat, and sweep out blockage if visible.').
treatment('Choking', 'If performing on infant (less than 1 year old): hold infant facedown on forearm (resting on thigh) and thump five times on middle of back with heel of hand. If this doesn\'t work, hold faceup on forearm, and compress chest with two fingers on center of breastbone five times.').

treatment('Electric Shock','Examine, but don\'t touch(with bare hands).').
treatment('Electric Shock','Turn off source of electricity if possible, or move source away from person with object made of non-conducting material (cardboard, wood, plastic).').
treatment('Electric Shock','Check for breathing, coughing, or movement; perform CPR if none.').
treatment('Electric Shock','Lay person down and position head lower than trunk, with legs elevated.').
treatment('Electric Shock','Don\'t move person unless in immediate danger.'). 

treatment('Fracture','Stop bleeding; apply pressure to wound with sterile bandage or clean cloth.').
treatment('Fracture','Immobilize injured area; don\'t try to put bone back into position.').
treatment('Fracture','Apply ice packs to limit swelling.').
treatment('Fracture','Treat for shock: if person is faint and has short, rapid breaths, lay down with head lower than trunk and elevate legs.').

treatment('Frostbite','Protect skin from further exposure; get out of cold, and don\'t use frostbitten appendages if possible.').
treatment('Frostbite','Gradually warm frostbitten areas in warm water slightly above body temperature; don\'t use direct heat.').
treatment('Frostbite','Don\'t thaw affected areas if they\'ll freeze again; wrap them up if already thawed.').
treatment('Frostbite','Get emergency help if numbness or pain remains during warming, or blisters develop.').

treatment('Head Trauma', 'Keep person still and quiet; don\'t move, and elevate head and shoulders.').
treatment('Head Trauma', 'Stop bleeding; apply pressure to wound with sterile gauze or clean cloth, unless  you suspect a skull fracture.').
treatment('Head Trauma', 'If the person is not breathing, coughing or moving, begin CPR.').

treatment('Heart Attack', 'Call 911 or drive to hospital (preferably with someone else driving).').
treatment('Heart Attack', 'Chew aspirin, if not allergic.').
treatment('Heart Attack', 'Take nitroglycerin, if prescribed.').
treatment('Heart Attack', 'Perform CPR if the person is unconcious, and advised by EMS.').

treatment('Heat Exhaustion/Stroke', 'Get person out of sun.').
treatment('Heat Exhaustion/Stroke', 'Lay person down; elevate legs.').
treatment('Heat Exhaustion/Stroke', 'Loosen or remove clothing.').
treatment('Heat Exhaustion/Stroke', 'Give them non-alcholic, non-caffinated beverages.').
treatment('Heat Exhaustion/Stroke', 'Cool by spraying with cool water; fan person.').

treatment('Hypothermia', 'Call 911.').
treatment('Hypothermia', 'Move person out of cold, or protect them from wind, cover head and insulate from ground. Handle gently.').
treatment('Hypothermia', 'Don\'t apply direct heat, don\'t massage, and don\'t warm arms and legs. Apply warm compresses to heck, neck, chest and groin.').
treatment('Hypothermia', 'If not vomiting, drink warm, non-alcoholic drink.').

treatment('Puncture Wound', 'Stop bleeding; apply gentle pressure with clean cloth or sterile bandage. Seek emergency assistance if bleeding persists.').
treatment('Puncture Wound', 'Clean wound with clear water, and use sterilized (i.e. with alcohol) tweezers to remove small particles. Call doctor if debris remain in wound; clean area around wound with soap and clean cloth.').
treatment('Puncture Wound', 'Change the bandage at least once daily, or when wet or soiled. ').
treatment('Puncture Wound', 'Watch for signs of infection, like redness, drainage or swelling.').

treatment('Severe Bleeding', 'Have the person lie down and cover them to conserve body heat. Position their head at at lower level than the trunk or elevate the legs and site of the bleeding.').
treatment('Severe Bleeding', 'With gloves on, remove obvious dirt and debri; don\'t remove deeply embedded objects.').
treatment('Severe Bleeding', 'Staunch bleeding by applying pressure to wound until bleeding stops. Use a sterile bandage or clean cloth and hold pressure for at least 20 minutes, regardless of whether bleeding ceases. Maintain pressure by binding wound with bandage, gauze or cloth and tape; use hands if you must, but preferably wear gloves or a clean plastic bag.').
treatment('Severe Bleeding', 'Don\'t remove the gauze or the bandage, even if bleeding soaks the bandage or cloth. Add more absorbent material instead.').
treatment('Severe Bleeding', 'Squeeze a main artery if direct pressure fails to stop bleeding. IF the wound is on the arm, pinch the pressure point above the elbow or below the armpit; if the wound is on the leg, pinch the pressure point behind the knee and in the groin, keeping your fingers flat and your other hand exerting pressure on the wound.').
treatment('Severe Bleeding', 'When bleeding stops, immobilize the injured body part, but leave the bandages on. Bring the person to the emergency room.').
treatment('Severe Bleeding', 'If you suspect internal bleeding, call 911.').

treatment('Snakebite','Immobilize the bitten arm or leg, and remain quiet.').
treatment('Snakebite','Position the area of the bite at or below the level of your heart.').
treatment('Snakebite','Cleanse the wound but don\'t flush it with water; cover it with a clean, dry dressing.').
treatment('Snakebite','Apply a splint; don\'t use a tourniquet or ice.').
treatment('Snakebite','Don\'t cut the wound or attempt to remove venom.').
treatment('Snakebite','Don\'t drink caffeine or alcohol.').
treatment('Snakebite','Try to remember the color and shape of the snake, and call 911.').

%Runs the program.
run :-
	format('FIRST AID DIAGNOSIS SYSTEM\nSYMPTOM INPUT: To answer yes, type \'y.\'; otherwise, type \'n.\'.\n',[]),
	input,
	bagof(X, diagnosis(X), Y), 
	!,
	confidence_sort(Y, Z),
	output(Z).
	
%Finds all the symptoms and associated questions and proceeds to ask the questions and receive input.
input :- bagof(A, B^question(A, B), C), 
	bagof(E, D^question(D, E), F),
	input(C, F).
input([S|TS],[Q|TQ]) :-
write(Q),
read(Z),
processinput(S, Z),
input(TS, TQ).
input([], []).
	
%Processes the input of symptoms and determines whether to continue or abort.
processinput(X,'y') :- assert(exhibitsSymptom(X)).
processinput(_,'q') :- fail, !.
processinput(_, _).


%Finds the confidence of a diagnosis, which is the proportion of exhibited symptoms of a diagnosis to the total number of symptoms of that diagnosis.
confidence(W, X) :- findall(U, (exhibitsSymptom(U), isSymptomOf(U, W)), L1), length(L1, Y), findall(V, isSymptomOf(V, W), L2), length(L2, Z), X is 100*Y/Z.


%Sorts list by confidence.
confidence_sort(List,Sorted):-c_sort(List,[],Sorted).
c_sort([],Acc,Acc).
c_sort([H|T],Acc,Sorted):-insert(H,Acc,NAcc),c_sort(T,NAcc,Sorted).
   
insert(X,[Y|T],[Y|NT]):- confidence(X, V),
confidence(Y, W),
 W>V,
 insert(X,T,NT).

insert(X,[Y|T],[X,Y|T]):- 
confidence(X, V),
confidence(Y, W), 
W=<V.

insert(X,[],[X]).

%Outputs the list of diagnoses and treatments in descending order of confidence.
output([]).
output([A|T]):-
printDiagnosis(A),
bagof(C, isSymptomOf(C, A), B), !,
format('  Symptoms:\n',[]),
printSymptoms(B),
bagof(D, treatment(A,D), E),
format('  Recommended Actions:\n',[]),
printTreatments(E), 
write('Get next most likely diagnosis? (y.|n.)'), nl, !,
read(F),
process2(F,T).

%Processes input and determines whether to continue to output symptoms.
process2('y',T) :- output(T).
process2(_,_) :- retractall(exhibitsSymptom(_)), abort.

%Prints a diagnosis and its confidence.
printDiagnosis(X) :-
confidence(X, Y),
format('Diagnosis: ~w\nConfidence: ~2f%\n', [X, Y]).

%Prints the list of symptoms of a diagnosis.
printSymptoms([]).
printSymptoms([X|T]) :-
exhibitsSymptom(X),
format('    - ~w', [X]), nl,
printSymptoms(T).
printSymptoms([_|T]) :- 
printSymptoms(T).

%Prints a list of treatments.
printTreatments([]).
printTreatments([X|T]) :-
	format('    - ~s', [X]), nl,
	printTreatments(T).
