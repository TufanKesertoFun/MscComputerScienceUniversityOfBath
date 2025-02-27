:- module(double_quotes, []).

/** <module> Readable string notation

The ISO Prolog standard defines three meanings for double quoted
strings in Prolog text (double quoted list tokens 6.4.6).  The Prolog
flag double_quotes (7.11.2.5) determines the current meaning.  This
flag affects compilation, read_term/3, and companions - any
conversions from Prolog text to terms.  The possible values are:
chars, codes, atom.

==
   double_quotes     "abc".      "".
  ------------------------------------
       chars        [a,b,c].     [].
       codes       [97,98,99].   [].    % ASCII/Latin/Unicode
       atom           abc.       ''.
==

Once the Prolog text "abc" has been converted to the corresponding
term, the information where the term came from is lost forever.  This
is particularly cumbersome when using DCGs with texts.  Consider with
default settings:

==
?- phrase(("Ja tvoi ",("sluga"|"rabotnik"),"!"), Satz).
Satz = [74,97,32,116,118,111,105,32,115,108,117,103,97,33] ;
Satz = [74,97,32,116,118,111,105,32,114,97,98,111,116,110,105,107,33].
==

Shall Satz be printed as quoted text or not?  It really depends on the
situation.  In the context of clpfd-constraints, an unwanted
conversion is very inconvenient.  The situation is less problematic
when using chars.  Even if we have written a list [a,b,c] on purpose,
"abc" is not too far away - and a bit more compact.

With set_prolog_flag(double_quotes,chars) in the .plrc or pl.ini we
get now:

==
?- phrase(("Ja tvoi ",("sluga"|"rabotnik"),"!"), Satz).
Satz = "Ja tvoi sluga!" ;
Satz = "Ja tvoi rabotnik!".
==

2010-11-24: Chars in place of codes.

@author Ulrich Neumerkel

*/

:- multifile(user:portray/1).

user:portray(Chars) :-
	Chars = [A|_],
	atom(A),
	current_prolog_flag(double_quotes, chars),
	is_of_type(chars, Chars),
	!,
	atom_chars(Atom,Chars),
	write_double_quoted_atom(Atom).

write_double_quoted_atom(Atom) :-
	must_be(atom,Atom),
	with_output_to(chars([Ch0|Chars]),writeq(Atom)),
	(	Ch0 == '\''
	->	phrase(quoted_dbl(Chars), S)
	;	phrase(unquoted_dbl([Ch0|Chars]), S)
	),
	format('"~s"',[S]).

unquoted_dbl([]) -->
	[].
unquoted_dbl([C|Cs]) -->
	( {C == (\)} -> [\] ; [] ),
	[C],
	unquoted_dbl(Cs).

quoted_dbl(['\'']) --> !.
quoted_dbl([\,\|Cs]) --> !,
	[\,\],
	quoted_dbl(Cs).
quoted_dbl([\,'\'']) --> !,
	[\].
quoted_dbl([\,'\''|Cs]) --> !,
	['\''],
	quoted_dbl(Cs).
quoted_dbl([C|Cs]) -->
	( { C == '"' } -> [\] ; [] ),
	[C],
	quoted_dbl(Cs).
