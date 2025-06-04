# coding: utf-8
"""
Bibliotheque 'computer' qui contient l'interface pour les
méthodes de l'IA en mode 'IA vs Human'.
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

import tkinter as tk
import re,unicodedata

from configs import *
from random import choice
from handledico import Handle_DicoMotus
from gameboard import GameBoard

class IA_Computer():
    
    def __init__(self, master:tk.Tk, gameboard:GameBoard):
        
        self.__master = master
        self.__name:str = "Jarvis"
        self.__gameboard = gameboard
        self.__nb_tries = gameboard.nb_Tries
        self.__nb_letters = gameboard.nb_Letters
        self.__dico_Buttons = self.__gameboard.dico_Buttons     # - dictionnaire des boutons du mot IA_word
        self.__dico_Motus:Handle_DicoMotus = master.dico_MOTUS  # - recupere le Handle_DicoMotus() du parent
        self.__search_word:list = (['.',]* self.__nb_letters)   # - lettres du mot collectées à leur position
        self.__is_word_letters = set()                          # - set() des lettres contenues dans le mot 
        self.__IA_status:PlayerStatus = "idle"                  # - status du joueur 'IA_Computer'
        # ------------ Mot de l'utilisateur que doit trouver l'IA -------------
        self.__MOTUS_word:str = ""                  
        # -------- Création liste de mots et 1er mot proposé par l'IA ---------
        self.__init_WordsList()
        # ---------------------------------------------------------------------
    
    @property
    def IA_status(self) -> PlayerStatus:
        return self.__IA_status
    @IA_status.setter
    def IA_status(self, ia_status:PlayerStatus):
        self.__IA_status = ia_status 
    
    def __init_WordsList(self):
        self.__list_IA_Words:list = self.__dico_Motus.dico_MOTUS[f"{self.__nb_letters}"]                
        self.__IA_word:str = self.__dico_Motus.dico_MOTUS_one_word(f"{self.__nb_letters}")

    def __find_free_word(self, IA_word:str) -> list:
        """ Recherche de la position du premier mot libre de 0 à 6...9 """
        dummy:list = ([])
        records = list(filter(lambda record:record[1]==False, self.__dico_Buttons.values()))
        if records:
            for l in range(self.__nb_letters):
                btn_ID, tries, letter = records[l][0]   # -> 'records[l][0]:(18, 3, ' ')'
                self.__dico_Buttons[tries,l] = ((btn_ID,tries,IA_word[l]), True, records[l][2])
                self.__dico_Buttons[tries,l][2].configure(text=IA_word[l].upper())
                dummy.append(((btn_ID,tries,IA_word[l]),records[l][2]))
            self.TR = records[0][0][1]
        return self.TR, dummy
    
    def valide_Mot(self, human_word) -> PlayerStatus:
        self.IA_status = "idle"
        self.__MOTUS_word = human_word
        # -----------------------------------------------------------------
        while self.IA_status == "idle":
            self.OK,self.IS,self.NO = 0, 0, 0
            word_nbr, buttons = self.__find_free_word(self.__IA_word)
            # -- recherche des lettres qui ne sont pas dans le mot MOTUS --
            NO_letters = self.__look_for_NO_letters(self.__MOTUS_word, self.__IA_word)
            buttons = self.__draw_NO_letters(bad_letters=NO_letters, buttons=buttons)
            IS_letters = self.__look_for_IS_letters(self.__MOTUS_word, self.__IA_word)
            buttons = self.__draw_IS_letters(is_letters=IS_letters, buttons=buttons)
            OK_letters = self.__look_for_OK_letters(self.__MOTUS_word, self.__IA_word)
            buttons = self.__draw_OK_letters(ok_letters=OK_letters, buttons=buttons)
            # -----------------------------------------------------------------            
            if self.OK == self.__nb_letters:
                self.IA_status = "winner"
            elif word_nbr == self.__nb_tries-1 and self.OK < self.__nb_letters:
                self.IA_status = "loser"
            else:    
                try:
                    self.__IA_word = choice(self.__list_IA_Words)
                except IndexError:
                    self.__init_WordsList()
        # ------------- Reinitialisation de la liste des mots -------------                            
        self.__init_WordsList()
        return self.IA_status
    
    def __draw_NO_letters(self, bad_letters:list, buttons:list):
        for idx,button in buttons:
            """ Affiche la/les lettre(s) qui n'est/ne sont pas dans le 
                mot et change la couleur de fond, le relief de ces lettres.
            """
            if button.cget('text').lower() in bad_letters:
                button.configure(bg=COLOR_NO,relief='flat',activebackground=COLOR_NO)    
                button.flash()
                self.NO += 1
        return buttons
        
    def __draw_IS_letters(self,is_letters:list, buttons:list):
        """ Affiche la/les lettre(s) qui est/sont dans le mot  
            et change la couleur de fond, le relief de ces lettres.
        """
        for idx,button in buttons:
            if button.cget('text').lower() in list(map(lambda l:l[1], is_letters)):
                button.configure(bg=COLOR_IS,relief='flat',activebackground=COLOR_IS)
                button.flash()
                self.IS += 1
        return buttons
        
    def __draw_OK_letters(self, ok_letters:list, buttons:list):
        """ Affiche la/les lettre(s) du mot qui est/sont bien placée(s)
            et change la couleur de fond, le relief de ces lettres.
        """
        for idx,button in buttons:
            #print(f"idx: {idx} -/- ok_letters: {ok_letters}")
            if (idx[0] % self.__nb_letters, idx[2]) in  ok_letters:
                button.configure(bg=COLOR_OK,relief='flat',activebackground=COLOR_OK)
                button.flash()
                self.OK += 1
        return buttons
        
    def __update_search_OK_words(self, letters:list):
        """ Crée le pattern du mot à rechercher pour mettre à jour
            la liste des mots pour poursuivre la recherche.
        """
        for letter in letters:
            self.__search_word[letter[0]] = letter[1]
        pattern = "".join([w for w in self.__search_word])
        self.__list_IA_Words = [word.group() for word in [re.match(pattern,word) \
                                                   for word in self.__list_IA_Words] if word != None]
    
    def __look_for_OK_letters(self,ref_word:str, test_word:str) -> list:
        """ Retourne uniquement les lettres de 'test_word' présentes dans 'ref_word' 
            bien placées avec leur place dans le mot.
        """
        ok_letters = list(map(lambda s:s[0], list(filter(lambda w:w[0]==w[1], \
                                        list(zip(enumerate(ref_word),enumerate(test_word)))))))
        if ok_letters:
            self.__update_search_OK_words(ok_letters)
        return ok_letters
        
    def __look_for_NO_letters(self,ref_word:str, test_word:str) -> list:
        """ Retourne uniquement les lettres de 'test_word' non présentes dans 'ref_word'
            Met à jour la liste des mots sans ceux dont les lettres ne sont pas dans
            le mot de référence, ici le mot MOTUS.
        """
        bad_letters = list(set(test_word) - set(ref_word))
        self.__list_IA_Words = list(filter(lambda word:len([w for w in word \
                                               if w in bad_letters]) == 0 ,self.__list_IA_Words))        
        return bad_letters
    
    def __look_for_IS_letters(self,ref_word:str, test_word:str) -> list:
        """ Retourne uniquement les lettres de 'test_word' présentes dans 'ref_word' 
            et rajoute le tuple (None, letter) au set() 'self.__current_word'.
        """
        is_letters = list(zip([None]*self.__nb_letters,list((set(ref_word) & set(test_word)))))
        if is_letters:
            self.__is_word_letters.update(is_letters)
            self.__list_IA_Words = list(filter(lambda word:[w for w in word \
                                       if w in map(lambda s:s[1], self.__is_word_letters)],self.__list_IA_Words))
            #print(f"is_letters: {self.__is_word_letters} ---> len(self.__list_IA_Words): {len(self.__list_IA_Words)}")
        return is_letters
