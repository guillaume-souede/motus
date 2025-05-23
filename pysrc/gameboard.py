#!/usr/bin/python3
# -- encode utf-8 --
"""
Bibliotheque 'GameBoard'. Surcharges de la classes tKinter Frame
pour afficher le tableau de lettres du jeu MOTUS inspiré du jeu 
télévisé diffusé sur France2.
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

from gui_tools import *
from configs import *

class GameBoard(My_LabelFrame):
    
    def __init__(self, master, dico_buttons:dict, nb_letters:int=6, nb_tries:int=6, *args, **kwargs):
        
        self.__master = master
        self.__nb_tries = nb_tries
        self.__nb_letters = nb_letters
        self.__dico_buttons = dico_buttons
        
        tab_options:dict = {'bg':'orange','bd':5,'relief':'groove','labelanchor':'n'}
        for key in list(tab_options.keys()):
            if kwargs.get(key, None) == None: kwargs[key] = tab_options.get(key, None)
        My_LabelFrame.__init__(self, master, name='gameBoard', pad=(2,2,0,0), *args, **kwargs)
        self.update()

    @property
    def nb_Letters(self) -> int:
        return self.__nb_letters
    @nb_Letters.setter
    def nb_Letters(self, nb_letters):
        self.__nb_letters = nb_letters
        
    @property
    def nb_Tries(self) -> int:
        return self.__nb_tries
    @nb_Tries.setter
    def nb_Tries(self, nb_tries):
        self.__nb_tries = nb_tries

    @property
    def dico_Buttons(self) -> dict:
        return self.__dico_buttons 

    def __delete_DicoButtons(self):
        [value[2].destroy() for value in self.__dico_buttons.values()]
        self.__dico_buttons.clear()

    def create_GameBoard(self, dimensions:tuple, nb_letters:int, nb_tries:int) -> dict:
        ltr_size = [42,44,42,40]
        self.__delete_DicoButtons()
        padXY = [(28,0),(20,1),(10,4),(4,6)]
        self.nb_Letters = nb_letters if nb_letters != self.nb_Letters else self.nb_Letters
        self.nb_Tries = nb_tries if nb_tries != self.nb_Tries else self.nb_Tries
        ltr_font = (f'Courier\ New {ltr_size[nb_letters-6]} bold italic')
        for i in range(nb_tries):
            for j in range(nb_letters):
                btn_ID = i * nb_letters + j if i != 0 else i * (nb_letters - 1) + j
                btn = tk.Button(self,bd=1,relief="raised",text="A",font=ltr_font)  #f"btn {btn_ID}"
                btn.grid(column=j, row=i, padx=1, ipadx=padXY[nb_letters-6][0], 
                                              pady=2, ipady=padXY[nb_letters-6][1], sticky="new")
                # ----- dico_buttons :   (n°Lettre, n°mot), état, objet) ------
                self.__dico_buttons[(i,j)] = ((btn_ID,i," "), False, btn)
        return self.__dico_buttons

if __name__ == "__main__":
    
    dico:dict = ({})
    
    root = tk.Tk()
    gameboard = GameBoard(root, dico, nb_letters=6, nb_tries=6, cspan=18, rspan=10)
    gameboard.create_GameBoard(gameboard.bbox(),gameboard.nb_Letters,gameboard.nb_Tries)
    print("self._dico_buttons:",[print(gameboard.dico_Buttons[i]) \
                for i in list(filter(lambda w:w[0]==0, gameboard.dico_Buttons.keys()))])
    root.mainloop()