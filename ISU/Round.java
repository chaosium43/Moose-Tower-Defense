package ISU;

import java.util.*;
// deals with the handling of moose sends
public class Round {
    private double evolution;
    private TileMap parent;
    private LinkedList<RoundSend> loadedSends; 
    private boolean trigger;

    private static String[] rounds = {
        "",
        "l 20 m 0 0 0.5 e",
        "l 30 m 0 0 0.4 e",
        "l 10 m 0 1 0.8 e",
        "l 10 m 0 0 0.5 m 0 0 0.5 m 0 1 0.5 e",
        "l 20 m 0 0 0.3 e l 10 m 0 1 0.5 e",
        "l 15 m 0 0 0.5 e l 5 m 0 2 0.5 e",
        "l 10 m 0 3 0.7 e",
        "l 8 m 0 4 0.8 e",
        "l 5 l 5 m 0 0 0.5 e m 0 2 0 e",
        "l 3 l 7 m 0 1 0.2 e m 0 1 1 e",
        "l 35 m 0 1 0.4 e l 9 m 0 3 1 e",
        "l 50 m 0 2 0.8 e",
        "l 10 l 2 m 0 3 0.5 e m 0 4 0.5 e",
        "l 25 m 0 3 0.4 e",
        "l 100 m 0 0 0.1 e",
        "l 25 m 0 4 0.4 e",
        "l 100 m 0 1 0.1 e",
        "l 10 m 0 5 1 e",
        "l 12 m 0 6 0.8 e",
        "l 5 m 1 6 2 e", 
        "l 7 l 4 m 1 1 0.5 e m 0 5 0 e",
        "l 30 m 0 5 0.25 e",
        "l 5 m 0 7 1 e",
        "l 5 m 0 7 1 e l 10 m 0 2 0.5 e l 5 m 0 4 0.5 e",
        "l 5 m 0 8 1 e",
        "l 10 m 0 7 0.5 m 0 8 0.5 e",
        "l 3 m 1 7 2 e l 3 m 1 8 2 e",
        "l 4 l 24 m 0 4 0.1 e m 0 4 1 e",
        "l 8 m 1 4 1 e",
        "l 10 m 0 9 1 e",
        "l 5 m 0 7 0.5 m 0 8 0.5 m 0 9 0.5 e",
        "l 8 m 0 10 1 e",
        "l 3 m 1 10 3 e",
        "l 30 m 0 4 0.25 e m 0 10 4 m 0 10 0.5 m 0 10 0.5",
        "l 10 m 0 11 0.5 e",
        "l 3 m 1 10 3 e l 50 m 0 4 0.2 e",
        "l 2 m 1 11 5 e",
        "m 1 0 1 m 1 1 1 m 1 2 1 m 1 3 1 m 1 4 1 m 1 5 1 m 1 6 1 m 1 7 1 m 1 8 1 m 1 9 1 m 1 10 1 m 1 11 1",
        "m 0 0 0",
        "m 2 0 0",
        "l 10 l 7 m 0 7 0.5 e m 0 11 0 e",
        "l 4 m 0 10 1 e l 4 m 1 10 1 e",
        "l 43 m 0 10 0.3 e",
        "l 10 l 10 m 0 4 0.25 e m 0 9 0 l 10 m 0 4 0.25 e m 0 11 0 e e",
        "l 5 m 2 0 5 e",
        "l 10 m 0 8 0.25 m 1 8 0.25 e",
        "l 3 m 2 0 1 e m 0 11 2 l 14 m 0 10 0.25 e",
        "l 7 m 1 7 1 m 1 8 1 m 1 9 1 e",
        "l 3 m 2 0 1 m 2 0 1 m 2 0 4 e",
        "l 10 m 2 0 2 e",
        "l 4 m 1 9 1 m 1 10 1 m 1 11 1 m 2 0 1 e",
        "l 5 l 4 m 0 11 0.5 e m 2 0 0.5 e",
        "l 13 m 0 0 0.1 e l 12 m 0 1 0.1 e l 11 m 0 2 0.1 e l 10 m 0 3 0.1 e l 9 m 0 4 0.1 e l 8 m 0 5 0.1 e l 7 m 0 6 0.1 e l 6 m 0 7 0.1 e l 5 m 0 8 0.1 e l 4 m 0 9 0.1 e l 3 m 0 10 0.1 e l 2 m 0 11 0.1 e m 2 0 0",
        "l 6 m 1 10 0.75 m 1 11 0.75 m 1 10 0.75 m 2 0 0.75 e",
        "l 15 m 2 0 1 e",
        "l 4 l 7 m 1 8 0.5 e m 2 0 0 e",
        "l 10 m 2 0 0.5 e",
        "l 20 m 1 11 0.4 e",
        "m 0 2 0",
        "m 3 0 0",
        "l 20 m 2 0 0.75 e",
        "l 10 l 10 m 0 7 0.25 e m 2 0 0 e",
        "l 5 m 2 0 0 1 l 50 m 0 10 0.25 e",
        "l 2 m 3 0 3 e",
        "l 100 m 0 11 0.05 e",
        "l 20 m 2 0 0.5 e",
        "l 3 m 3 0 5 e",
        "l 2 m 3 0 0 l 10 m 2 0 2 e e",
        "l 3 m 3 0 2 e l 10 m 2 0 1 e",
        "m 4 0 0",
        "l 3 m 4 0 3 e",
        "l 40 m 1 11 0.5 e",
        "l 5 m 3 0 2 e l 12 m 2 0 1 e",
        "l 50 m 2 0 0.25",
        "l 10 m 3 0 2 e",
        "l 200 m 1 8 0.01 e",
        "l 100 m 2 0 0.15 e",
        "l 20 m 3 0 0.5 e",
        "l 10 m 4 0 1 e",
        "l 10 l 3 m 4 0 1 e m 3 0 0 e"
    };

    private static String[] commentary = {
        "",
        "Welcome to Moose Tower Defense!\nIf you forgot how to play, feel free to reference the tutorial in the main menu!",
        "Not too bad so far, eh?",
        "Blue mooses move faster than red mooses and spawn a red moose when killed.\nJust one of the many moose variants to come.",
        "Sometimes, several kinds of mooses can be mixed together in waves!",
        "Friendly reminder to upgrade your towers to increase their popping power!\nYou can find the upgrades of a tower by clicking on it.",
        "Can you guess what green mooses do?",
        "How about yellows mooses?",
        "And what about pink mooses?",
        "You should be getting the hang of things at this point.\nPrepare for the mooses to become more organized in how they try to overrun your defenses.\nThey may try to send large hordes of weaker mooses or smaller amounts of stronger mooses.",
        "If you haven't noticed already, this game is heavily inspired by BTD5.\nYou will notice many Mooses have a corresponding BTD5 bloon equivalent.",
        "However, you will be in for a few surprises if you try to play this game only using BTD5 knowledge.",
        "Anwyays, congratulations on making it past the first 10 rounds. Have 50 green mooses as a reward.",
        "Now that you've had a break, it's time to get back to business, isn't it?",
        "\"We\" like to go fast, right? Incoming rush of fast packed bloons ahead!",
        "And now we go nice and slow...",
        "And fast again...",
        "And slow again...",
        "You're probably sick of all the packed bloons rounds.\nIt's time for a \"break\" with some spaced mooses, isn't it?",
        "Notice how the black mooses spawn two moose children instead of one when destroyed.\nMore powerful mooses will do this in the future as well.",
        "And sometimes, there are special kinds of mooses that will spawn even more than two children.",
        "Did you like those mini blimps? They have extra HP and spawn in three children when destroyed.",
        "You might have bought a cannon by now to deal with all the packed mooses.\nIf you did, I've got some bad news for you.",
        "And if you sold your cannons to get an alternative form of defense, I have more bad news for you.",
        "Ah, good 'ol leads. Immune to all sharp projectiles and releases two blacks upon defeat.",
        "Gunner towers provide GREAT firing power for a CHEAP PRICE!!!\nGet one this round, you won't regret it!\n(Sponsored by Gunner Tower Building Co.)",
        "Arctic mooses are basically lead mooses but evil.\nWhere leads are immune to all sharp attacks, arctics are immune to all non-sharp attacks.\nThey can also come in with leads, making for a potent and nasty combo.",
        "And this holds doubly true when they come in the form of mini blimps.",
        "You managed to survive all of that? You're quite persistent.\nHave 100 pink mooses as a reward I guess?",
        "Uhhhh, uhhhh, something something pink blimps go???",
        "What would happen if you stuffed a black moose and white moose together?\nIf you've played BTD5 before, I think you can guess this one.",
        "In case you need a refresher, a zebra combines the immunities of a black and white moose together.\nWhen destroyed, it also drops a black and white moose. Quite neat!",
        "Also, did I forget to mention that this game was developed during pride month?\nWow! If only there was a special moose to commemmorate this special event!",
        "Next up on Moose Tower Defense: 100 red blimps!!! Hooray!!!!",
        "Oh, I'm so \"sorry\" for lying to you the last round, but you can't expect me to hold your hand forever.\nSometimes, you have to learn things yourself and take what I say with a grain of salt.\nIf you know what I mean ;)",
        "Ceramics work the same way they do in BTD5. They take ten hits to destroy, and spawn two rainbows as children.\nDon't underestimate them, as they can easily overwhelm your defenses in later rounds!",
        "Did you know that this game has automatic saving?\nEvery time you complete a round, the game automatically saves your progress.\nThis means you can go back to your game you worked so hard on at any time!",
        "And yes, they are NOT exempt from being turned into blimps. Be warned!",
        "Blimp parade time! Let's enjoy the great variety in mooses and see blimps of every variety!",
        "The calm before the storm...",
        "If you're playing on Easy mode, this is the last round. To make sure the game isn't too easy, I have one last surprise for you ;)",
        "Congratulations on defeating your first Gamerr! However, things are not going to get easier from here.",
        "42 is the answer to the universe and everything. Including your defenses.\n(Fun fact round 42 was an overhyped round in BTD5 take what I say with a grain of salt)",
        "Did you know that Mrs. Wong is 43 years of age??!??!?!?!?!\n(She says she's 25 but she's LYING! Ask a man called Lenny Fang for more details.)\nAnyways, here are 43 rainbow mooses.",
        "Since I'm running out of things to say, I'll start telling you some fun facts about this game.",
        "My inspiration for creating this was a Scratch project I made back in middle school.",
        "It bears a similar name and theme to this wonderful game that you're playing right now!",
        "It's also inspired by a popular BTD5 Scratch ripoff and plays similarly to that.\n(BTW the link to that BT5 ripoff is here: https://scratch.mit.edu/projects/62965100/)",
        "This means that you would have to aim every tower manually, making micro management difficult.",
        "This game's track also has the colour scheme it has because of the old Moose Tower Defense's map.\n(It was an iceberg surrounded by water)",
        "Fun fact: 3 of the 4 towers in the original Moose Tower Defense made it into this game.\n(The Gunner, Energizer, and the Cannon)",
        "The fourth weapon was based off of the AWP from CSGO, and was the stand-in for the Super Monkey.",
        "Fun fact #2: Crushing attacks in this game were inspired by original MTD attacks.\nUnlike normal attacks which don't harm the same moose twice, attacks in the Scratch version always did.",
        "This was because lists in Scratch cannot be arbitrarily declared.\nAlso, keeping track of Moose clones is quite difficult as sprite values can't be stored in Scratch variables.",
        "Fun fact #3: You couldn't have too many of the same type of tower on screen\nThis is because scratch cloning is quite unreliable and if too many projectiles are cloned at once, only one tower fires.",
        "Fun fact #4: In that game, there were only two upgrades, and one upgrade path per tower.",
        "Fun fact #5: Mooses were implemented using costumes, so mooses could never drop more than 1 child.\nThis was inspired by how the BTD5 clone handled bloons.",
        "Now that I'm done yapping about the Scratch project, I'll yap some more about this game instead.",
        "A lot of hard work went into making this game!\nIn fact, there are about 3884 lines of code in this game as of writing this!",
        "There are also over 20 classes in this game. This game is very \"classy\"!\nHaha get it, classy?",
        "The second calm before the storm...",
        "If you decided to play on medium difficulty, this is the last round for you.\nI've got a second surprise cooked up for you ;)",
        "Did you enjoy that huge Gamerr? There will be a lot more in the coming rounds ;)",
        "Have you noticed cash getting tight these past few rounds?\nMooses have slowly rewarded less money for kills since round 20, and this trend will continue.",
        "Moose Tower Defense is a pretty good game. Going outside is pretty good too.\nConsider putting the game down for a bit and coming back to your save later if you've been playing fro a long time.",
        "A hundred ceramics could ruin your day, just sayin.",
        "Ceramics are pretty good but you know whats better? Gamerrs!!!! Yayyy!!!!!",
        "Enjoying this game so far? Consider showing it to a friend while you're at it!",
        "Are you also enjoying the hordes of Gamerrs?\nRounds are going to look a lot like that from now in.",
        "If you have trouble getting past later rounds, consider trying out different combinations of towers!\nEach tower has unique quirks and synergize differently with different towers.",
        "What would happen if you mixed a Gamerr, a pink, a black, and a lead at the same time?\nYou're about to figure out ;)",
        "Since I got lazy and uncreative, I decided to steal DDTs right from the Bloons franchise.\nHow original!",
        "I also got the inspiration for mini blimps in part from mini MOABs in Bloons Super Monkey 2.",
        "Furthermore, MOAB = Tiny Gamerr, BFB = Huge Gamerr",
        "Have you considered giving up yet?",
        "If you haven't considered giving up yet, the next rounds will change your mind.",
        "Upcoming: 200 tightly packed Arctic mini blimps",
        "Upcoming: 100 Tiny Gamerrs",
        "Upcoming: 20 Huge Gamerrs",
        "Upcoming: 10 DDTs",
        "After going through all those unfair trials, you still persist.\nLooks like I'll have to show you what hard difficulty REALLY means and make you quit!",
        "congrats yayaya"
    };

    public String getCommentary(int round) {
        return commentary[round];
    }

    public boolean evolve(double delta) {
        if (loadedSends.isEmpty()) { // round is... JOEVER!!!!
            return true;
        }

        RoundSend front = loadedSends.peek();
        evolution += delta;
        if (front.getDelay() <= evolution || trigger) {
            if (trigger) {
                evolution = 0;
                trigger = false;
            } else {
                evolution -= front.getDelay();
            }
            if (front.getVariant() == 0) {
                new Moose(parent, front.getType(), 0);
            } else if (front.getVariant() == 1) {
                new MiniBlimp(parent, front.getType(), 0);
            } else if (front.getVariant() == 2) { // tiny gamerr
                new TinyGamerr(parent, 0, 0);
            } else if (front.getVariant() == 3) {
                new HugeGamerr(parent, 0, 0);
            } else if (front.getVariant() == 4) {
                new DDT(parent, 0, 0);
            }
            loadedSends.poll();
        }

        return false;
    }

    public LinkedList<RoundSend> parseString(StringTokenizer st) { // parses a round given tokens
        LinkedList<RoundSend> s = new LinkedList<RoundSend>();
        while (st.hasMoreTokens()) {
            String id = st.nextToken();
            if (id.equals("m")) {
                int i1 = Integer.parseInt(st.nextToken());
                int i2 = Integer.parseInt(st.nextToken());
                double d = Double.parseDouble(st.nextToken());
                s.add(new RoundSend(i1, i2, d));
            } else if (id.equals("e")) {
                return s;
            } else if (id.equals("l")) {
                int n = Integer.parseInt(st.nextToken());
                LinkedList<RoundSend> l = parseString(st);
                for (int i = 0; i < n; i++) {
                    s.addAll(l);
                }
            }
        }
        return s;
    }

    public void startRound(int round) {
        loadedSends = parseString(new StringTokenizer(rounds[round], " "));
        evolution = 0;
        trigger = true;
    }

    public Round(TileMap parent) {
        loadedSends = new LinkedList<RoundSend>();
        this.parent = parent;
    }
}
