from typing import Literal

WordLength = Literal["6", "7", "8", "9"]
wordlengthlist:list=[c for c in range(6,10)]

PlayerStatus = Literal["winner","loser"]
PlayerGenre = Literal["homme","femme"]

dico_path:str = "data"
images_path:str  = "images"

default_dico_filename = "motsMotus.txt"
default_MOTUS_background = "defaut.png"

COLOR_OK = "lightgreen"
COLOR_IS = "tan"
COLOR_NO = "red"

if __name__ == "__main__":
    
    print(f"WordLength: {WordLength}")
    print(f"wordlengthlist: {wordlengthlist}")
    print(f"player_status: {PlayerStatus}")
    print(f"dico_path: {dico_path}")
    print(f"images_path: {images_path}")
    print(f"default_dico_filename: {default_dico_filename}")
    print(f"default_MOTUS_background: {default_MOTUS_background}")