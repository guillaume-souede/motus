#!/usr/bin/python3.9
# -*- coding: utf-8 -*-

import os, sys
import os.path as op

from unicodedata import normalize

source:str = "frgut.txt"
output:str = ""

sourceList:list = ([])

def read_source(source:str) -> list:
    
    dummylist:list = ([])
    
    filename = op.join(os.getcwd(),"data",source)
    print(f"source filename: {filename}")
    if op.isfile(filename):
        with open(filename, mode='rt', encoding='utf-8') as sourcefile:
            [dummylist.append(word) for word in sourcefile if len(word.strip()) in range(6,10)]
    else: print(f"bad filename: {filename}")
    return dummylist
            
def write_output(words:list):
    
    if output:
        filename = op.join(os.getcwd(),"data",output)
    else:
        name,ext = op.splitext(source)
        filename = op.join(os.getcwd(),"data",f"{name}_MOTUS{ext}")
        listToWrite = [normalize("NFC",word) for word in words]
        with open(filename, 'wt', encoding='utf-8') as motusfile:
            motusfile.writelines(listToWrite)
        
sourceList = read_source(source=source)
write_output(words=sourceList)