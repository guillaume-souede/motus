#!/usr/bin/python3
# -- encode utf-8 --
"""
Bibliotheque 'Handle_Dico'. Lecture et chargement du dictionnaire
des mots pour le jeu MOTUS inspiré du jeu télévisé diffusé sur France2.
Copyright (C) 2025  Bernard AMOUROUX

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

DNA_GBRecords_GUI v2 (C) 2025  Bernard AMOUROUX
This program comes with ABSOLUTELY NO WARRANTY; for details type `show w'.
This is free software, and you are welcome to redistribute it
under certain conditions; type `show c' for details.
"""

__author__ = "Bernard AMOUROUX"
__date__ = "$Date: 2025/05/18 07:00 $"
__copyright__ = "Copyright (c) 2025 Bernard AMOUROUX"
__license__ = "GPL 3"

import os.path as op
import tkinter as tk

from os import getcwd
from random import shuffle,choice,seed
from configs import *


class Handle_DicoMotus():
    
    def __init__(self, master:tk.Tk, filename:str=None):
        
        self.__master = master      # pour développement futur, sera déjà déclaré !
        self.__filename = filename if filename != None else default_dico_filename
        self.__dico_MOTUS:dict[str:[list]] = ({})
        self.__load_dicofile()
        # ---- for test only ----
        seed(1)
    
    def __load_dicofile(self):
        bad_chars = """ \n\r\t"""
        fname = op.join(getcwd(), dico_path, self.__filename)
        if op.isfile(fname):
            with open(fname, mode="rt", encoding='utf-8') as motusfile:
                for world in motusfile:
                    w = world.strip(bad_chars); l = len(w)
                    if not f"{l}" in self.__dico_MOTUS:
                        self.__dico_MOTUS[f"{l}"] = []
                    self.__dico_MOTUS[f"{l}"].append(w)
        else:
            raise FileNotFoundError(f" Fichier dictionnaire '{fname}' non trouvé !")
                    
    @property
    def dico_MOTUS(self)->dict:
        return self.__dico_MOTUS                    # return full dictionary
    
    def __dico_MOTUS_length(self, wordlength:WordLength)->list:
        """ Propriété qui renvoi la liste des mots du dictionnaire MOTUS 
            de longueur 'wordlength'.
        """
        return self.__dico_MOTUS.get(wordlength, [f"{wordlength}",])   # return value of dictionary[worldlength]
    
    def __shuffle_worlds(self, wordlength:WordLength)->list:
        """ Mélange la liste """
        shuffle(self.__dico_MOTUS_length(wordlength))
        return self.dico_MOTUS.get(wordlength, [f"{wordlength}",])
    
    def dico_MOTUS_one_world(self, wordlength:WordLength)->str:
        return choice(self.__shuffle_worlds(wordlength=wordlength))
    
    
    

if __name__ == "__main__":
    
    dico = Handle_DicoMotus(None, "motsMotus.txt")
    randomwords = [print(dico.dico_MOTUS_one_world(f"{l}")) for l in range(6,10)] 
