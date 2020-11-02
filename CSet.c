// On my honor:
//
// - I have not discussed the C language code in my program with
// anyone other than my instructor or the teaching assistants
// assigned to this course.
//
// - I have not used C language code obtained from another student,
// the Internet, or any other unauthorized source, either modified
// or unmodified.
//
// - If any C language code or documentation used in my program
// was obtained from an authorized source, such as a text book or
// course notes, that has been clearly noted with a proper citation
// in the comments of my program.
//
// - I have not designed this program in such a way as to defeat or
// interfere with the normal operation of the grading code.
//
// Dana Gurland
// dgurland
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <stdbool.h>
#include "CSet.h"
#define FILLER INT32_MIN    // sentinel value for unused array cells
int compare(const void * a, const void * b);
// CSet provides an implementation of a set type for storing a collection of
// signed 32-bit integer values (int32_t).
//
// The implementation imposes the following constraints:
//  - storage is array-based
//  - duplicate elements are not allowed in a CSet
//  - logically empty cells are set to INT32_MIN
//  - sets can contain up to UINT32_MAX elements
//  - unless noted to the contrary, the worst-case cost of each operation
//    is O(N), where N is the number of elements in the CSet object(s)
//    that are involved
//  - empty test is O( 1 )
//  - Contains() is O( log N )
//  - there are no memory leaks during any of the supported operations
//
// We say a CSet object A is proper if and only if it satisfies each of the 
// following conditions:
//
//  1.  If A.Capacity == 0 then A.Usage == 0 and A.Data == NULL.
//  2.  If A.Capacity > 0 then A.Data points to an array of dimension A.Capacity.
//  3.  A.Data[0 : A.Usage-1] are the values stored in the set 
//      (in unspecified order)
//  4.  A.Data[A.Usage : A.Capacity-1] equal INT32_MIN (FILLER)
//
// CSet objects that have not been initialized are said to be raw.
//
// With the sole exception of CSet_Init(), all CSet objects supplied as
// parameters are expected to be proper.
//
/**
struct _CSet {

   uint32_t Capacity;    // dimension of the set's array
   uint32_t Usage;       // number of elements in the set
   int32_t* Data;        // pointer to the set's array
};

typedef struct _CSet CSet;
*/
/**
 * Initializes a raw pSet object, with capacity Sz.
 *
 * Pre:
 *    pSet points to a CSet object, but *pSet may be proper or raw
 *    Sz   has been initialized
 * Post:  
 *    If successful:
 *       pSet->Capacity == Sz
 *       pSet->Usage == 0
 *       pSet->Data points to an array of dimension Sz, or
 *          is NULL if Sz == 0
 *       if Sz != 0, pSet->Data[0 : Sz-1] == INT32_MIN
 *    else:
 *       pSet->Capacity == 0
 *       pSet->Usage == 0
 *       pSet->Data == NULL
 * Returns:
 *    true if successful, false otherwise
 * 
 * Complexity:  O( Sz )
 */
bool CSet_Init(CSet* const pSet, uint32_t Sz){

	if(Sz > 0) {
		pSet->Capacity = Sz;
//		free(pSet->Data);
		pSet->Usage = 0;
//		pSet->Data = calloc(pSet->Capacity, sizeof(int32_t));
		int32_t* temp = (int32_t*)calloc(Sz, sizeof(int32_t));
		pSet->Data = temp;
		for(int i = 0; i < pSet->Capacity; i++) {
			pSet->Data[i] = INT32_MIN;
		}//for
		return true;
	}//if
	else {
		pSet->Data = NULL;
		pSet->Usage = 0;
		pSet->Capacity = 0;
		return false;
	}//else

}//init

/**
 * Adds Value to a pSet object.
 *
 * Pre:
 *    *pSet is proper
 *    Value has been initialized
 * Post:  
 *    If successful:
 *       Value is a member of *pSet
 *       pSet->Capacity has been increased, if necessary
 *       *pSet is proper
 *    else:
 *       *pSet is unchanged
 * Returns:
 *    true if successful, false otherwise
 * 
 * Complexity:  O( pSet->Usage )
 */
bool CSet_Insert(CSet* const pSet, int32_t Value){

	if(CSet_Contains(pSet, Value)) {
		return false;
	}
	
	if(pSet->Usage == pSet->Capacity){
		int32_t* newData = (int32_t*)realloc(pSet->Data, 2*pSet->Capacity);
		pSet->Capacity = pSet->Capacity * 2;
		*pSet->Data = newData;
	}//increase capacity
	pSet->Data[pSet->Usage] = Value;
	pSet->Usage = pSet->Usage + 1;
	qsort(pSet->Data, pSet->Usage, sizeof(int32_t), compare);
	return true;

}//insert

/**
 * Removes Value from a pSet object.
 *
 * Pre:
 *    *pSet is proper
 *    Value has been initialized
 * Post:  
 *    If Value was a member of *pSet:
 *       Value is no longer a member of *pSet
 *       pSet->Capacity is unchanged
 *       pSet->Usage is decremented
 *       *pSet is proper
 *    else:
 *       *pSet is unchanged
 * Returns:
 *    true if Value was removed, false otherwise
 * 
 * Complexity:  O( pSet->Usage )
 */
 bool CSet_Remove(CSet* const pSet, int32_t Value){

	 if(!CSet_Contains(pSet, Value)){
		 return false;
	 }//if
	 int32_t counter = 0;
	 bool found = false;
	 while(!found && counter < pSet->Usage){
		if(pSet->Data[counter] == Value) {
			found = true;
			pSet->Data[counter] = pSet->Data[pSet->Usage - 1];
			pSet->Data[pSet->Usage - 1] = FILLER;
			pSet->Usage = pSet->Usage - 1;
		}//if
		counter = counter + 1;
	 }//while
	 qsort(pSet->Data, pSet->Usage, sizeof(int32_t), compare);
	return found;
 }//remove

/**
 * Determines if Value belongs to the given pSet object.
 *
 * Pre:
 *    *pSet is proper
 *    Value has been initialized
 * Post:  
 *    *pSet is unchanged
 * Returns:
 *    true if Value belongs to *pSet, false otherwise
 * 
 * Complexity:  O( log(pSet->Usage) )
 */
bool CSet_Contains(const CSet* const pSet, int32_t Value){
	int32_t *found;
	found = (int32_t*) bsearch(&Value, pSet->Data, pSet->Usage, sizeof(int32_t), compare);
	if (found != NULL) {
		return true;
	}
	else {
		return false;
	}
}//contains

/**
 * Determines if two CSet objects contain the same elements.
 *
 * Pre:
 *    *pA and *pB are proper
 * Post:  
 *    *pA is unchanged
 *    *pB is unchanged
 * Returns:
 *    true if sets contain same elements, false otherwise
 * 
 * Complexity:  O( pA->Usage )
 */
bool CSet_Equals(const CSet* const pA, const CSet* const pB){

	bool equal = true;
	int32_t counter = 0;
	if(pA->Usage != pB->Usage) {
		return false;
	}
	while(equal && counter < pA->Usage) {
		if(pA->Data[counter] != pB->Data[counter]){
			equal = false;
		}//if
		counter = counter + 1;
	}//
	return equal;
}//equals

/**
 * Determines if one CSet object is a subset of another.
 *
 * Pre:
 *    *pA and *pB are proper
 * Post:  
 *    *pA is unchanged
 *    *pB is unchanged
 * Returns:
 *    true if *pB contains every element of *pA, false otherwise
 * Complexity:  O( pA->Usage )
 */
bool CSet_isSubsetOf(const CSet* const pA, const CSet* const pB){

	if(pA->Usage > pB->Usage) {
		return false;
	}
	bool equal = true;
	int32_t counter = 0;
	while(equal && counter < pA->Usage){
		if(!CSet_Contains(pB, pA->Data[counter])){
			equal = false;
		}//if
		counter = counter + 1;
	}//while
	return equal;
}//subset

/**
 * Sets *pUnion to be the union of the sets *pA and *pB.
 *
 * Pre:
 *    *pUnion, *pA and *pB are proper
 * Post:
 *    *pA and *pB are unchanged
 *    For every integer x, x is contained in *pUnion iff x is contained in
 *       *pA or in *pB (or both).
 *    pUnion->Capacity == pA->Capacity + pB->Capacity
 *    pUnion->Usage    == pA->Usage + pB->Usage - number of elements that
 *                        occur in both *pA and *pB
 *    *pUnion is proper
 * Returns:
 *    true if the union is successfully created; false otherwise
 * 
 * Complexity:  O( max(pA->Usage, pB->Usage) )
 */
bool CSet_Union(CSet* const pUnion, const CSet* const pA, const CSet* const pB){
	CSet* temp = malloc(sizeof(CSet));
	CSet_Init(temp, pA->Capacity + pB->Capacity);
	int max = pA->Usage;
	if(pB->Usage > pA->Usage) {
		max = pB->Usage;
	}//max;

	for(int i = 0; i < max; i++) {
		if (i < pA->Usage) {
			CSet_Insert(temp, pA->Data[i]);
		}
		if (i < pB->Usage) {
			CSet_Insert(temp, pB->Data[i]);
		}
	}//for
	CSet_Copy(pUnion, temp);
	return true;
}//union
 
/**
 * Sets *pSym to be the symmetric difference of the sets *pA and *pB.
 *
 * Pre:
 *    *pSym, *pA and *pB are proper
 * Post:
 *    *pA and *pB are unchanged
 *    For every integer x, x is contained in *pSym iff x is contained in
 *       *pA but not in *pB, or x is contained i *pB but not in *pA.
 *    pDiff->Capacity == pA->Capacity + pB->Capacity
 *    pDiff->Usage    == pA->Usage - number of elements that
 *                        occur in exactly one of *pA and *pB
 *    *pSym is proper
 * Returns:
 *    true if the difference is successfully created; false otherwise
 * 
 * Complexity:  O( max(pA->Usage, pB->Usage) )
 */
bool CSet_SymDifference(CSet* const pSym, const CSet* const pA, const CSet* const pB){
	CSet* temp = malloc(sizeof(CSet));
	CSet_Init(temp, pA->Capacity + pB->Capacity);
	int max = pA->Usage;
	if(pB->Usage > max) {
		max = pB->Usage;
	}//max

	for(int i = 0; i < max; i++){
		if (i < pA->Usage && !CSet_Contains(pB, pA->Data[i])) {
			CSet_Insert(temp, pA->Data[i]);
		}
		if (i < pB->Usage && !CSet_Contains(pA, pB->Data[i])) {
			CSet_Insert(temp, pB->Data[i]);
		}

	}//for
	CSet_Copy(pSym, temp);
	return true;
}

/**
 * Makes a deep copy of a CSet object.
 *
 * Pre:
 *    *pTarget and *pSource are proper
 * Post:  
 *    *pSource is unchanged
 *    If successful:
 *       pTarget->Capacity == pSource->Capacity
 *       pTarget->Usage == pSource->Usage
 *       pTarget[0:pTarget->Capacity-1] ==  pSource[0:pSource->Capacity-1]
 *       pTarget->Data != pSource->Data, unless pTarget == pSource
 *       *pTarget is proper.
 *    else:
 *       *pTarget is unchanged
 * Returns:
 *    true if successful, false otherwise
 * 
 * Complexity:  O( max(pSource->Usage) )
 */
bool CSet_Copy(CSet* const pTarget, const CSet* const pSource){
	if (pTarget == pSource) {
		return false;
	}
	CSet_Init(pTarget, pSource->Capacity);
	for(int i = 0; i < pSource->Usage; i++) {
		CSet_Insert(pTarget, pSource->Data[i]);
	}//for
	
	return true;
}

/**
 *  Reports the current capacity of a pSet object.
 *
 *  Pre:
 *     *pSet is proper
 *  Post:
 *     *pSet is unchanged
 *  Returns:
 *     pSet->Capacity
 * 
 * Complexity:  O( 1 )
 */
uint32_t CSet_Capacity(const CSet* const pSet){
	return(pSet->Capacity);
}//capacity

/**
 *  Reports the number of elements in a pSet object.
 *
 *  Pre:
 *     *pSet is proper
 *  Post:
 *     *pSet is unchanged
 *  Returns:
 *     pSet->Usage
 * 
 * Complexity:  O( 1 )
 */
uint32_t CSet_Usage(const CSet* const pSet){
	return(pSet->Usage);
}//usage

/**
 *  Determines whether a CSet object is empty.
 *
 *  Pre:
 *     *pSet is proper
 *  Post:
 *     *pSet is unchanged
 *  Returns:
 *     true if pSet->Usage == 0, false otherwise
 * 
 * Complexity:  O( 1 )
 */
bool CSet_isEmpty(const CSet* const pSet){
	return(pSet->Usage == 0);
}//empty

int compare(const void *a, const void * b) {
	return (*(int*)a - *(int*)b );
}
