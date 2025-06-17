# coding: utf-8
"""
Bibliotheque 'GUI_Tools'. Quelques surcharges de classes tKinter 
pour le jeu MOTUS inspiré du jeu télévisé diffusé sur France2
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

'configs' library for MOTUS v3.0 (C) 2025  Bernard AMOUROUX
This program comes with ABSOLUTELY NO WARRANTY; for details type `show w'.
This is free software, and you are welcome to redistribute it
under certain conditions; type `show c' for details.
"""

__author__ = "Bernard AMOUROUX"
__date__ = "$Date: 2025/05/18 07:00 $"
__copyright__ = "Copyright (c) 2025 Bernard AMOUROUX"
__license__ = "GPL 3"

import os
import os.path as op
import pickle as pkdata

from typing import Literal

WordLength = Literal["6", "7", "8", "9"]
wordlengthlist:list=[c for c in range(6,10)]

PlayerMode = Literal["human","computer","fighters"]
gamemodelist:list = ["Humain vs IA","IA vs Humain","Humain vs Humain"]
Gamehardness = Literal["easy","normal","hardu","terrible","infaisable"]
gamehardlist:list = ["easy","normal","hardu","terrible","infaisable"]

PlayerStatus = Literal["winner","loser","idle"]
PlayerGenre = Literal["homme","femme","lgbt"]

dico_path:str = "data"
images_path:str  = "images"

default_help_filename = "rules.txt"
default_dico_filename = "frgut_MOTUS.txt"
default_MOTUS_background = "defaut.png"
default_param_filename = "motus_cfg.dat"

COLOR_OK = "red"
COLOR_IS = "lightblue"
COLOR_NO = "yellow"


class App_Options(object):
    
    def __init__(self):
        # -------------------------------------------------
        self.__dico_path:str = dico_path
        self.__images_path:str = images_path
        # -------------------------------------------------
        self.__params_fname:str = default_param_filename
        self.__dico_filename:str = default_dico_filename
        self.__help_filename:str = default_help_filename
        self.__back_filename:str = default_MOTUS_background
        # -------------------------------------------------
        self.__accent_char:int = 0      # 1 = avec caractères accentués
        # -------------------------------------------------
        self.__nb_letters:int = 6       # -- nombre de caractères défaut
        self.__nb_tries:int = 6         # -- nombre maxi propositions 
        # -------------------------------------------------
        self.__fullscreen:bool = False  # -- bascule mode plein écran/fenêtré
        self.__game_difficulty:Gamehardness = "easy" 
        self.__game_mode:PlayerMode = "human"
    
    @property
    def fullscreen(self) -> bool:
        return self.__fullscreen
    @fullscreen.setter
    def fullscreen(self, fullscreen):
        self.__fullscreen = fullscreen
    
    @property
    def difficulty(self) -> Gamehardness:
        return self.__game_difficulty
    @difficulty.setter
    def difficulty(self, difficulty:Gamehardness):
        self.__game_difficulty = difficulty
    
    @property
    def gamemode(self) -> PlayerMode:
        return self.__game_mode
    @gamemode.setter
    def gamemode(self, gamemode:PlayerMode):
        self.__game_mode = gamemode    
    
    @property
    def nb_letters(self) -> int:
        return self.__nb_letters
    @nb_letters.setter
    def nb_letters(self, letters:int):
        self.__nb_letters = letters
        
    @property
    def nb_tries(self) -> int:
        return self.__nb_tries
    @nb_tries.setter
    def nb_tries(self, tries:int):
        self.__nb_tries = tries
    
    @property
    def accentchar(self) -> int:
        return self.__accent_char
    @accentchar.setter
    def accentchar(self, accent:int):
        self.__accent_char = accent    
        
    @property
    def paramfilename(self) -> str:
        return self.__params_fname
    @paramfilename.setter
    def paramfilename(self, filename:str):
        self.__params_fname = filename
        
    @property
    def dicopath(self) -> str:
        return self.__dico_path
    @dicopath.setter
    def dicopath(self, pathname:str):
        self.__dico_path = pathname
        
    @property
    def imagepath(self) -> str:
        return self.__images_path
    @imagepath.setter
    def imagepath(self, pathname):
        self.__images_path = pathname
        
    @property
    def dicofilename(self) -> str:
        return self.__dico_filename
    @dicofilename.setter
    def dicofilename(self, filename:str):
        self.__dico_filename = filename
        
    @property
    def helpfilename(self) -> str:
        return self.__help_filename
    @helpfilename.setter
    def helpfilename(self, filename:str):
        self.__help_filename = filename
        
    @property
    def backfilename(self) -> str:
        return self.__back_filename
    @backfilename.setter
    def backfilename(self, filename:str):
        self.__back_filename = filename
        
    def __str__(self) -> str:
        return "\n".join([f"{key[12:]:17}: {getattr(self, key)}" for key in self.__dict__.keys()])
            
        
        
class Saveload_CFG(object):

    def __init__(self):
        self.__filename = op.join(os.getcwd(),dico_path, default_param_filename)    
        # --- Constructeur 'self.__options' avec les paramètres par défaut ----
        self.__options = App_Options()
        self.__parameters = [getattr(self.__options, key) for key in self.__options.__dict__.keys()]
        # ---------------------------------------------------------------------
        self.__options.paramfilename = op.basename(self.__filename)
 
    @property
    def options(self) -> App_Options:
        return self.__options
    @options.setter
    def options(self, parameters:list):
        [setattr(self.__options, key, param) for key,param in list(zip(self.__options.__dict__.keys(),parameters))]
    
    @property
    def parameters(self) -> list:
        return [getattr(self.__options, key) for key in self.__options.__dict__.keys()]
        
    def cfg_backup(self):
        try:
            #print(f"Saveload_CFG.cfg_backup(): {self.parameters}")
            with open(self.__filename, 'wb') as outputfic:
                pkdata.dump(self.parameters, outputfic, pkdata.HIGHEST_PROTOCOL )
        except FileNotFoundError as msg:
            print(f" backup error {os.path.basename(self.__filename)}: {msg}")
            return False
        return True
        
    def cfg_load(self):
        if not op.isfile(self.__filename):
            self.cfg_backup()
        try:
            with open(self.__filename, 'rb') as inputfic:
                self.__parameters = pkdata.load(inputfic)
        except FileNotFoundError as msg:
            print(f" load error {os.path.basename(self.__filename)}: {msg}")            
            return False
        # -------- Paramètres sauvegardés ---------
        if self.__parameters:
            self.options = self.__parameters
            return True
        # -----------------------------------------
        return False


if __name__ == "__main__":
    
    print(f"WordLength: {WordLength}")
    print(f"wordlengthlist: {wordlengthlist}")
    print(f"player_status: {PlayerStatus}")
    print(f"player_mode: {PlayerMode}")

    config = Saveload_CFG()
    if config.cfg_load():
        print(f"Options:\n{config.options}\nParameters: {config.parameters}")
    else:
        print(f"Bad config file")
