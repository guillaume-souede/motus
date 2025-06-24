#!/usr/bin/python3
# coding: utf-8
# commande à taper en ligne de commande après la sauvegarde de ce fichier:
# python setup.py build
import os,sys
from cx_Freeze import setup, Executable

# Dependencies are automatically detected, but it might need fine tuning.
# "packages": ["os"] is used as example only
# Définir les chemins vers Tcl/Tk
tcl_dir = '/usr/share/tcltk/tcl8.6'
tk_dir = '/usr/share/tcltk/tk8.6'

# Ajouter les variables d'environnement dans l'exécutable
os.environ['TCL_LIBRARY'] = tcl_dir
os.environ['TK_LIBRARY'] = tk_dir

build_exe_options = {
                         "packages":['tkinter','computer','configs','gameboard','gui_tools','handledico','human'],
                         "include_files":[(tcl_dir, 'tcl8.6'),(tk_dir, 'tk8.6'),"../data",
                                                        "../images","../README.md","Licence_gpl-3.0.txt"],
                         "excludes": ["../old_files","../bin","../src","../audio","setup.py",
                                      "motus_v4.0.00.py","configs.py",'computer.py',
                                      'gameboard.py','gui_tools.py','handledico.py','human.py']
                     }

# base="Win32GUI" should be used only for Windows GUI app
base = None
if sys.platform == "win32":
    base = "Win32GUI"

setup(
    name = "Projet MOTUS",
    version = "4.0",
    description = "Un dérivé du jeu télévisé de France Télévision 'MOTUS'",
    options = {"build_exe": build_exe_options},
    executables = [Executable(  script = "motus_v4.0.00.py",
                                icon = "../images/motus.gif",
                                base = base)]
      )
