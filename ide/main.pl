% https://www.ic.unicamp.br/~meidanis/courses/mc336/2009s2/prolog/problemas/

% P1 - ACCEPTED
my_last(H, [H]).
my_last(L, [_|T]) :- my_last(L, T).

:- my_last(L, [66,88, 456]), write(L), nl.

% P2 - ACCEPTED
my_last_but_one(H, [H, T]).
my_last_but_one(H, [A, B| T]) :- my_last_but_one(H, [B | T]).

:- my_last_but_one(L, [66,9000,3333]), write(L), nl.

% P3 - ACCEPTED
element_at(Element, [Element| _], 1).
element_at(Element, [_|T], Pos) :- PrevPos is Pos - 1, element_at(Element, T, PrevPos).

:- element_at(X,[a,b,c,d,e],2), write(X), nl.

% P4 - ACCEPTED
length([], 0).
length([H|T], N) :- length(T, S), N is S + 1.

:- length([a, b], X), write(X), nl.

% P5 - ACCEPTED
append([], L, L).
append([H|T], L, R) :- append(T, L, TL), R = [H|TL].

reversed_list([], []).
reversed_list([H|T], R) :- reversed_list(T, ReversedTail), append(ReversedTail,[H],R).

:- reversed_list([z, 0, m, uu, k3], L), write(L), nl.

% P6 - ACCEPTED
palindrome(List) :- reversed_list(List, Reversed), List == Reversed.

palindrome_check(L) :- write(L), (palindrome(L) -> write(' is palindrome'); write(' is NOT palindrome')), nl.
:- palindrome_check([1, 2, 1]).
:- palindrome_check([1, 2]).
:- palindrome_check([9999999]).
:- palindrome_check([34]).
:- palindrome_check([6, 7, 0]).
:- palindrome_check([6, 8, 8, 6]).

% P07 - Accepted
is_list([]).
is_list([H|T]) :-  is_list(T).

is_not_list(T) :- T \= [], T \= [A|B].

my_flatten([],[]).
my_flatten([A|T],R) :- is_not_list(A), my_flatten(T, TT), R = [A|TT].
my_flatten([A|T],R) :- is_list(A), my_flatten(A,AF), my_flatten(T, TT), append(AF, TT, R).

:- my_flatten([[a|[]], [[b, kk, 0], [c, d], [e, ii]]], X), write(X), nl.

% P08 - ACCEPTED

compress([], []).
compress([A], [A]).
compress([A|[A|C]], R) :- compress([A|C], R).
compress([A|[B|C]], R) :- A \== B, compress([B|C], K), append([A],K, R).

:-compress([a,a,a,a,b,c,c,a,a,d,e,e,e,e],X), write(X), nl.

% P9 - ACCEPTED
prefix_ref([], [], [], _).
prefix_ref([H|T], P, L, R) :- H == R, prefix_ref(T, P2, L, R), P = [H|P2].
prefix_ref([H|T], P, L, R) :- H \== R, P = [], L = [H|T].

prefix([], [], []).
prefix([H|T], P, L) :- prefix_ref([H|T], P, L, H).

:- prefix([a,a,a,a,b,c,c,a,a,d,e,e,e,e],X, L), write(X), nl, write(L), nl.

pack([], []).
pack([H|T], Packed) :- prefix([H|T], Prefix, Left), pack(Left, LeftPacked), append([Prefix], LeftPacked, Packed).

:- pack([a,a,a,a,b,c,c,a,a,d,e,e,e,e],X), write(X), nl.
%X = [[a,a,a,a],[b],[c,c],[a,a],[d],[e,e,e,e]]

% P10 - ACCEPTED
encode_packed([], []).
encode_packed([H|T], X) :- _ = [H|T], H = [E|_],
length(H, N), Block = [N, E], encode_packed(T, EncodedTail), X = [Block| EncodedTail].

encode(L, X) :- pack(L, P), encode_packed(P, X).

:- encode([a,a,a,a,b,c,c,a,a,d,e,e,e,e],X), write(X), nl.
%X = [[4,a],[1,b],[2,c],[2,a],[1,d][4,e]]

% P11 - ACCEPTED
simplified_encoded_list([], []).
simplified_encoded_list([[N,E]|T], X) :- N == 1, simplified_encoded_list(T, TT), X = [E|TT].
simplified_encoded_list([[N,E]|T], X) :- N > 1, simplified_encoded_list(T, TT), X = [[N,E]|TT].

encode_modified(L, X) :- encode(L, LEncoded), simplified_encoded_list(LEncoded, X).

:- encode_modified([a,a,a,a,b,c,c,a,a,d,e,e,e,e],X), write(X), nl.
%X = [[4,a],b,[2,c],[2,a],d,[4,e]]

% P12 - ACCEPTED
decode([],[]).
decode([H|T], X) :- is_list(H), H = [N, E], N > 1, P is N - 1, decode([[P, E]| T], Decoded), X = [E|Decoded].
decode([H|T], X) :- is_list(H), H = [N, E], N == 1, decode(T, Decoded), X = [E|Decoded].
decode([H|T], X) :- is_not_list(H), decode(T, Decoded), X = [H|Decoded].

:- decode([[4, a], b, [2, c], [2, a], d, [4, e]], X), write(X), nl.
%X = [a,a,a,a,b,c,c,a,a,d,e,e,e,e]

% P13 - ACCEPTED
encode_direct_acc([],X, Acc) :- reversed_list(Acc, AccRev), simplified_encoded_list(AccRev, X).
encode_direct_acc([H|T],X, [[N, H]|AccT]) :- P is N + 1, encode_direct_acc(T, X, [[P, H]|AccT]).
encode_direct_acc([H|T],X, Acc) :- encode_direct_acc(T, X, [[1, H]|Acc]).

encode_direct(L, X) :- encode_direct_acc(L, X, []).

:- encode_direct([a,a,a,a,b,c,c,a,a,d,e,e,e,e],X), write(X), nl.
% X = [[4,a],b,[2,c],[2,a],d,[4,e]]

% P14 - ACCEPTED
dupli([],[]).
dupli([A|B],X):- dupli(B,XX), append([A,A], XX, X).

:- dupli([a,b,c,c,d],X), write(X), nl.
% X = [a,a,b,b,c,c,c,c,d,d]

% P15 - ACCEPTED
repeat(Element, 0, []).
repeat(Element, 1, [Element]).
repeat(Element, N, X) :- N > 1, Pred is N - 1, repeat(Element, Pred, XX), X = [Element| XX].

dupli([], N, []).
dupli([H|T], N, X) :- repeat(H, N, HH), dupli(T, N, TT), append(HH, TT, X).

:- dupli([a,b,c],3,X), write(X), nl.
% X = [a,a,a,b,b,b,c,c,c]


% P16 - ACCEPTED
drop_helper([], N, [], Acc).
drop_helper([H|T], N, R, Acc) :- Acc == 1, drop_helper(T, N, R, N).
drop_helper([H|T], N, R, Acc) :- Acc \== 1, Pred is Acc - 1, drop_helper(T, N, R2, Pred), R = [H|R2].

drop(L, N, R) :- drop_helper(L, N, R, N).

:- drop([a,b,c,d,e,f,g,h,i,k],3, X), write(X), nl.
% X = [a,b,d,e,g,h,k].

% P17 - ACCEPTED
split_acc([], [], [], Acc).
split_acc([H|T], L1, L2, Acc) :- Acc > 0, Pred is Acc - 1, split_acc(T, K, L2, Pred), L1 = [H|K].
split_acc([H|T], L1, L2, Acc) :- Acc == 0, L2 = [H|T], L1 = [].

split(L,N,L1,L2) :- split_acc(L, L1, L2, N).

:- split([a,b,c,d,e,f,g,h,i,k],3,L1,L2), write(L1), nl, write(L2), nl.
%L1 = [a,b,c]
%L2 = [d,e,f,g,h,i,k]

% P18 - ACCEPTED
cut_front([], N ,[]).
cut_front([H|T], N, X) :- N > 0, P is N - 1, cut_front(T, P, X).
cut_front([H|T], N, X) :- N == 0, X = [H|T].

slice(L, From, To, R) :- FromDec is From - 1, length(L, Len),
Pos is Len - To, cut_front(L, FromDec, Cutted), reversed_list(Cutted, CuttedRev),
cut_front(CuttedRev, Pos, Cutted2), reversed_list(Cutted2, R).

:- slice([a,b,c,d,e,f,g,h,i,k],3,7,L), write(L), nl.
%X = [c,d,e,f,g]

% P19 - ACCEPTED
rotate(L, N, X) :- N < 0, PN is abs(N), length(L, Len), Pos is Len - PN,
split(L, Pos, L1, L2), append(L2, L1, X).
rotate(L, N, X) :- N > 0, split(L, N, L1, L2), append(L2, L1, X).

:- rotate([a,b,c,d,e,f,g,h],-2,X), write(X), nl.
%X = [g,h,a,b,c,d,e,f]

:- rotate([a,b,c,d,e,f,g,h],3,X), write(X), nl.
%X = [d,e,f,g,h,a,b,c]

% P20 - ACCEPTED
remove_at(X, [], N, []).
remove_at(X, [H|T], N, R) :- N > 1, P is N - 1, remove_at(X, T, P, R2), R = [H|R2].
remove_at(X, [H|T], N, R) :- N == 1, R = T, X = H.

:- remove_at(X,[a,b,c,d],2,R), write(R), nl.
%X = b
%R = [a,c,d]

dec(A, B) :- B is A - 1.
inc(A, B) :- B is A + 1.

insert_at(E, [H|T], N, R) :- N > 1, dec(N, P), insert_at(E, T, P, R2), R = [H|R2].
insert_at(E, T, N, R) :- N == 1, R = [E|T].

% P21 - ACCEPTED
:- insert_at(alfa,[a,b,c,d],2,L), write(L), nl.
%L = [a,alfa,b,c,d]

% P22 - ACCEPTED
range(From, To, L) :- From < To, inc(From, FromInc), range(FromInc, To, L2), L=[From|L2].
range(From, To, L) :- From == To, L = [From].

:- range(4,9,L), write(L), nl.
% L = [4,5,6,7,8,9]

% P23 - ACCEPTED
rnd_select_acc(L, N, R, Acc) :- N == 0, R = Acc.
rnd_select_acc(L, N, R, Acc) :- N > 0, dec(N, P),
length(L, Len), random_between(1, Len, K),
remove_at(E, L, K, LL), rnd_select_acc(LL, P, R, [E|Acc]).

rnd_select(L, N, R) :- rnd_select_acc(L, N, R, []).

:- rnd_select([a,b,c,d,e,f,g,h],3,L), write(L), nl.
% L = [e,d,a]

% P24 - ACCEPTED
rnd_select(N, M, L) :- range(1,M,R), rnd_select(R, N, L).

% :- rnd_select(6,49,L), write(L), nl.
:- rnd_select(6,12,L), write(L), nl.
%L = [23,1,17,33,21,37]

% P25 - ACCEPTED
rnd_permu(L, R) :- length(L, Len), rnd_select(L, Len, R).

:- rnd_permu([a,b,c,d,e,f],L), write(L), nl.
% L = [b,a,d,c,e,f]

% P26 - ACCEPTED
member(A, [A|G]).
member(A, [H|T]) :- member(A, T).

gen(U, L, N) :- range(U,L,A), member(N, A).

permutation(N,L,R) :- N == 0, R = [].
permutation(N,L,R) :- N > 0, length(L, Len), dec(N, P),
gen(1, Len, K), remove_at(E, L, K, LL), permutation(P, LL, C), append([E], C, R).

combination(N,L,R) :- N == 0, R = [].
combination(N,L,R) :- N > 0, length(L, Len), dec(N, P),
gen(1, Len, K), remove_at(E, L, K, LL), cut_front(L, K, LLL) ,combination(P, LLL, C), append([E], C, R).

%:- findall(L, combination(2,[a,b,c],L), Collected), length(Collected, Len), write(Collected), nl.
%:- findall(L, combination(3,[a,b,c,d,e,f],L), Collected), length(Collected, Len), write(Len), nl.

% P27 - ACCEPTED

subtract([], Delete, []).
subtract([H|T], Delete, Result) :- memberchk(H, Delete), subtract(T, Delete, Result).
subtract([H|T], Delete, Result) :- \+ memberchk(H, Delete), subtract(T, Delete, R2), Result = [H|R2].

:- subtract([aldo,beat,carla,david],[beat,david, aldo], X), write(X), nl.

group3(L,G1,G2,G3) :-
combination(2, L, G1), subtract(L, G1, LL),
combination(3, LL, G2), subtract(LL, G2, LLL),
combination(4, LLL, G3).

:- group3([aldo,beat,carla,david,evi,flip,gary,hugo,ida],G1,G2,G3)
, write(G1), write(' '),  write(G2), write(' '),  write(G3), write(' '), nl. %2,3,4

group_acc(L, [], Acc, Acc).
group_acc(L, [H|T], G, Acc) :- combination(H, L, G1), subtract(L, G1, LL),group_acc(LL, T, G, [G1|Acc]).

group(L, P, G) :- reversed_list(P, Prev), group_acc(L, Prev, G, []).

:- group([aldo,beat,carla,david,evi,flip,gary,hugo,ida],[1,1,1,1,1,1,1,1,1],Gs), write(Gs), nl.

% Gs = [[aldo,beat],[carla,david],[evi,flip,gary,hugo,ida]]

% P28 - ACCEPTED
% selection sort
min_element([E],E, Any).
min_element([H|T],E, Comparator) :- min_element(T, M, Comparator), (call(Comparator, M, H) -> E = M; E = H).

index_of(E, [E|T], 1).
index_of(E, [H|T], I) :- E \== H, index_of(E, T, I2), I is I2 + 1.

sort([], [], Any).
sort([H|T], Sorted, Comparator) :-
Unsorted = [H|T],
min_element(Unsorted,M, Comparator),
index_of(M, Unsorted, I),
remove_at(M, Unsorted, I, UnsortedWithoutElement),
sort(UnsortedWithoutElement, SortedRest, Comparator),
Sorted = [M|SortedRest].

:- sort([4,7,2,8,1,6,34,2,0], L, '<'), write(L), nl.

comparing_length(L1, L2) :- length(L1, Len1), length(L2, Len2), Len1 < Len2.
lsort(Unsorted, Sorted) :- sort(Unsorted, Sorted, comparing_length).

:- lsort([[a,b,c],[d,e],[f,g,h],[d,e],[i,j,k,l],[m,n],[o]],L), write(L), nl.
% L = [[o], [d, e], [d, e], [m, n], [a, b, c], [f, g, h], [i, j, k, l]]

increment_value_in_map([], K, [pair(K,1)]).
increment_value_in_map([pair(K,V)| T], K, UpdatedMap) :- inc(V, VV), UpdatedMap = [pair(K,VV)| T].
increment_value_in_map([pair(K,V)| T], S, UpdatedMap) :-
    K \= S,
    increment_value_in_map(T, S, UpdatedMapT), UpdatedMap = [pair(K,V)| UpdatedMapT].

%:- increment_value_in_map([pair(a,1), pair(b,8)], g ,M), write(M), nl.

len_frequency_map_acc([], Acc, Acc).
len_frequency_map_acc([H|T], Map, Acc) :-
    length(H, Len),
    increment_value_in_map(Acc, Len, MapUpdated),
    len_frequency_map_acc(T, Map, MapUpdated).
len_frequency_map(L, M) :- len_frequency_map_acc(L, M, []).

value(K, [pair(K, V)|T], V).
value(S, [pair(K, V)|T], VV) :- S \== K, value(S, T, VV).

comparing_frequency(L, L1, L2) :-
    len_frequency_map(L, FMap),
    length(L1, Len1),
    length(L2, Len2),
    value(Len1, FMap, L1Freq),
    value(Len2, FMap, L2Freq),
    L1Freq < L2Freq.
lfsort(Unsorted, Sorted) :- sort(Unsorted, Sorted, comparing_frequency(Unsorted)).

%:- lfsort([[a,b,c],[d,e],[f,g,h],[d,e],[i,j,k,l],[m,n],[o]],L), write(L), nl.
% L = [[i, j, k, l], [o], [a, b, c], [f, g, h], [d, e], [d, e], [m, n]]

%:- true, loves(A,B), write('-'), write(A),  write(B), nl, fail.
print_variables([]).
print_variables([K=V|T]) :- write(K), write(' = '), write(V), nl, print_variables(T).

top(Atom) :- \+((
    write('?- '),
    write(Atom), nl,
    read_term_from_atom(Atom ,Term, [variable_names(Names)]),
        call(Term),
        print_variables(Names),
        write('---'),nl,
        fail
    )
).

% :- top('combination(3,[a,b,c,d,e,f],L).').

% !!!! Have unit tests before proceeding !!!!!

