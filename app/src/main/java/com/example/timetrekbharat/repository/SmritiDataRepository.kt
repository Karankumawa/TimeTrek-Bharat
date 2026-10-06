package com.example.timetrekbharat.repository

import com.example.timetrekbharat.model.BardicSeal
import com.example.timetrekbharat.model.DepthTier
import com.example.timetrekbharat.model.StoryPersona
import com.example.timetrekbharat.model.SmritiArtifact

class SmritiDataRepository {

    fun getSampleVaultArtifacts(): List<SmritiArtifact> {
        return listOf(
            SmritiArtifact(
                id = "kumbhalgarh_badal_mahal",
                name = "Badal Mahal (Palace of Clouds)",
                hindiName = "बादल महल - कुंभलगढ़ दुर्ग",
                locationEra = "Kumbhalgarh Fort, Mewar • 15th-19th Century CE",
                imageResOrUrl = "app_logo",
                epigraphyOriginal = "𑀅𑀥𑀺𑀧𑀢𑀺 𑀭𑀸𑀡𑀸 𑀓𑀼𑀁𑀪 𑀯𑀺𑀭𑀘𑀺𑀢𑀁 𑀤𑀼𑀭𑀽𑀠𑀁 𑀩𑀸𑀤𑀮 𑀧𑁆𑀭𑀸𑀲𑀸𑀤",
                epigraphyDeciphered = "अधिपति महाराणा कुंभा विरचितं दुर्धर्षं बादल प्रसादम् (The impregnable Cloud Palace erected under the sovereign patron Maharana Kumbha)",
                acousticResonanceDesc = "Natural air-funneling turrets create high-frequency wind resonance, amplifying whispers up to 45 meters across marble corridors.",
                hydraulicMonsoonLevel = 90,
                narratives = mapOf(
                    Pair(DepthTier.QUICK, StoryPersona.BARD) to "• Perched at 3,600 feet atop the Aravallis, Badal Mahal caught monsoon winds to cool royal queens.\n• Birthplace of Maharana Pratap (1540 CE).\n• Surrounded by a 36 km long defense wall, second only to the Great Wall of China.",
                    Pair(DepthTier.QUICK, StoryPersona.ARCHITECT) to "• Built using double-tiered pastel turquoise and saffron corridors.\n• Utilized hollow terracotta wall vents for micro-climate breeze circulation.\n• Load-bearing archways eliminate timber beams, surviving centuries of monsoons.",
                    Pair(DepthTier.QUICK, StoryPersona.SENTRY) to "• Unmatched 360° optical vantage over the Thar Desert and Mewar passes.\n• Concentric parapets engineered with downward arrow-slits to repel cavalry night charges.\n• Secret escape tunnel leads 12 km subterraneanly down to Maharani Lake.",
                    Pair(DepthTier.QUICK, StoryPersona.CHRONICLER) to "• The crowning jewel of Maharana Kumbha's 32 Mewar fortresses.\n• Impregnable fortress never breached by force throughout three centuries of imperial sieges.",

                    Pair(DepthTier.TALE, StoryPersona.BARD) to "Perched at 3,600 feet above sea level, the Badal Mahal floating amidst cloud vapors was built by the visionary Maharana Kumbha and renovated by Maharana Fateh Singh. Here in 1540 CE, amidst the chant of bards, Maharana Pratap took his first breath. Local folklore sings of queenly chambers where turquoise stone walls caught monsoon breezes, while court troubadours played rudra veena melodies echoing over the 36-kilometer Great Wall of Mewar.",
                    Pair(DepthTier.TALE, StoryPersona.ARCHITECT) to "Badal Mahal exemplifies high-altitude Mewari palace engineering. Divided into Mardana (men's section) and Zanana (women's section), the palace incorporates ventilated stone jali screens, vaulted dome ceilings, and lime-mortar joinery that actively resists seismic shifts. The airflow channels inside the zanana bath chambers act as an organic air conditioning system powered purely by wind gradients.",
                    Pair(DepthTier.TALE, StoryPersona.SENTRY) to "Standing sentry atop Badal Mahal, a scout can glimpse enemy dust clouds 20 miles away towards Marwar. The entrance gates feature double-curved L-shaped blind corridors, forcing approaching besiegers to expose their unshielded right flanks to archers stationed on stone machicolations above. Under cover of darkness, signal fires lit here instantly alerted distant hill outposts.",
                    Pair(DepthTier.TALE, StoryPersona.CHRONICLER) to "In imperial chronicles, Kumbhalgarh is recorded as the invincible shelter of Mewar royalty during times of crisis. Rising above cloud lines, Badal Mahal symbolized royal authority. Multiple imperial armies attempted blockade assaults, but its elevated cisterns and impenetrable ramparts proved mathematically unassailable without treachery.",

                    Pair(DepthTier.CHRONICLE, StoryPersona.BARD) to "[Architecture & Engineering]\nBadal Mahal features two distinct stories housing Mardana Mahal and Zanana Mahal. Intricate pastel frescoes and marble jalis adorn the inner chambers.\n\n[The Living Legend]\nBards recount how Maharana Pratap's mother Maharani Jaiwanta Bai sang warrior lullabies in these breezy halls, instilling unwavering patriotism that would later ignite the Battle of Haldighati.\n\n[Dynastic Timeline]\n1458 CE: Maharana Kumbha completes fort foundation.\n1540 CE: Birth of Maharana Pratap.\n1885 CE: Maharana Fateh Singh restores cloud balconies.\n\n[Hidden Detail to Observe]\nLook closely at the Zanana stone screen: tiny carved peacock motifs serve as secret peep-holes through which royal women observed court ceremonies without being seen.",
                    Pair(DepthTier.CHRONICLE, StoryPersona.ARCHITECT) to "[Architecture & Engineering]\nConstructed entirely from local granite blocks joined with lime and jaggery mortar. The two-story structure features hemispherical domes and arched balconies engineered to withstand high wind velocity.\n\n[Hydraulic & Microclimate Engineering]\nSubterranean clay pipes drew rainwater into concealed cisterns beneath the palace courtyard, maintaining cooler interior temperatures year-round.\n\n[Dynastic Timeline]\n15th Century: Structural plan designed by chief architect Mandan.\n\n[Hidden Detail to Observe]\nExamine the ceiling vault junctions—smooth concave acoustic baffles throw sound across 40 feet with minimal decay.",
                    Pair(DepthTier.CHRONICLE, StoryPersona.SENTRY) to "[Architecture & Engineering]\nStrategic positioning on the highest peak of Kumbhalgarh Fort. The single narrow stairwell entrance creates an extreme defensive bottleneck.\n\n[The Living Legend]\nSentry records detail how 50 archers at Badal Mahal successfully held off a vanguard of 2,000 soldiers during the 1578 siege by raining burning oil down narrow stone conduits.\n\n[Dynastic Timeline]\n1578 CE: Siege of Kumbhalgarh by Shahbaz Khan.\n\n[Hidden Detail to Observe]\nNotice the iron ring brackets embedded near outer window ledges—used for anchoring defensive leather shields during artillery bombardment.",
                    Pair(DepthTier.CHRONICLE, StoryPersona.CHRONICLER) to "[Architecture & Engineering]\nA masterpiece of hill-fort design spanning 36 kilometers of continuous bastion walls with 360 fortified towers.\n\n[The Living Legend]\nChronicles from Delhi court emissaries describe Kumbhalgarh as 'the mountain eagle's nest that no imperial army could starve into submission'.\n\n[Dynastic Timeline]\n1448-1458 CE: Initial construction period.\n\n[Hidden Detail to Observe]\nThe strategic alignment between Badal Mahal and Chittorgarh's Vijay Stambha enabled optical mirror signaling across 80 kilometers of terrain."
                )
            ),
            SmritiArtifact(
                id = "rani_ki_vav",
                name = "Rani ki Vav (The Queen's Stepwell)",
                hindiName = "रानी की वाव - पाटन, गुजरात",
                locationEra = "Patan, Gujarat • 11th Century Solanki Dynasty",
                imageResOrUrl = "placeholder_heritage",
                epigraphyOriginal = "𑀰𑁆𑀭𑀻 𑀉𑀤𑀬𑀫𑀢𑀻 𑀭𑀸𑀚𑁆ñ𑀻 𑀓𑀸𑀭𑀺𑀢𑀁 𑀅𑀦𑀼 me 𑀲𑀼𑀦𑁆𑀤𑀭𑀁 𑀯𑀸𑀧𑀻",
                epigraphyDeciphered = "श्री उदयमती राज्ञी कारितं अतिसुन्दरं वापी (Erected by Queen Udayamati in memory of King Bhimdev I)",
                acousticResonanceDesc = "Multi-tiered subterranean stone terraces produce rich 7-tier echo reverberations when devotional mantras are chanted at the well base.",
                hydraulicMonsoonLevel = 95,
                narratives = mapOf(
                    Pair(DepthTier.QUICK, StoryPersona.BARD) to "• A subterranean inverted temple 27 meters deep built by Queen Udayamati in 1063 CE.\n• Over 500 principal sculptures depicting Lord Vishnu's avatars.\n• UNESCO World Heritage site celebrating sacred water conservation.",
                    Pair(DepthTier.TALE, StoryPersona.BARD) to "Conceived as an inverted temple honoring sacred water, Rani ki Vav was built by Queen Udayamati as a loving memorial for King Bhimdev I of the Solanki dynasty. Descending seven stories into the earth, it holds over 500 exquisite sculptures of Lord Vishnu in various avatars, guarded by celestial apsaras and nagakanyas, blending spiritual devotion with life-giving subterranean hydro-engineering.",
                    Pair(DepthTier.CHRONICLE, StoryPersona.BARD) to "[Architecture & Engineering]\nDesigned as an inverted temple with seven levels of pillared pavilions, descending down to a deep circular water well.\n\n[The Living Legend]\nWhen the Saraswati River flooded and buried the stepwell under silt for 700 years, local bards preserved songs describing 'the golden palace hidden under sand', leading archaeologists to its rediscovery in 1980.\n\n[Dynastic Timeline]\n1063 CE: Construction commissioned by Queen Udayamati.\n13th Century: Saraswati river silt covers stepwell.\n2014 CE: Declared UNESCO World Heritage Site."
                )
            ),
            SmritiArtifact(
                id = "hampi_vittala_temple",
                name = "Vittala Temple Musical Pillars",
                hindiName = "विट्ठल मंदिर संगीतमय स्तंभ - हम्पी",
                locationEra = "Hampi, Karnataka • 15th-16th Century Vijayanagara Empire",
                imageResOrUrl = "placeholder_heritage",
                epigraphyOriginal = "𑀲𑀗𑁆𑀕𑁆𑀻𑀢 𑀲𑁆𑀢𑀫𑁆𑀪 𑀯𑀺𑁆𑀞𑀮 𑀤𑁂𑀯𑀸𑀮𑀬",
                epigraphyDeciphered = "संगीत स्तंभ विट्ठल देवालय (Musical Stone Pillars of Vittala Temple)",
                acousticResonanceDesc = "56 solid granite pillars carved with variable density produce natural notes of Veena, Jalatarang, Damaru, and Mridangam when gently tapped.",
                hydraulicMonsoonLevel = 40,
                narratives = mapOf(
                    Pair(DepthTier.QUICK, StoryPersona.ARCHITECT) to "• 56 monolithic musical pillars (Saptaswara) tuned to Indian classical musical notes.\n• Carved from single granite blocks without hollow chambers.\n• Features iconic Stone Chariot (Ratha) dedicated to Garuda.",
                    Pair(DepthTier.TALE, StoryPersona.ARCHITECT) to "The Ranga Mantapa of Vittala Temple showcases legendary acoustical masonry. Each master pillar is surrounded by 7 smaller slender columns carved out of a single piece of granite. When tapped, the columns resonate with distinct musical frequencies corresponding to classical instruments, proving the Vijayanagara sculptors understood resonant frequency, rock density, and sound acoustics centuries ahead of modern science.",
                    Pair(DepthTier.CHRONICLE, StoryPersona.ARCHITECT) to "[Architecture & Engineering]\nMonolithic granite pillars engineered with varying thickness and stone density. The pillars emit distinct acoustic frequencies (Sa, Re, Ga, Ma, Pa, Dha, Ni).\n\n[The Living Legend]\nLegend states British officers cut open two pillars expecting to find copper wires inside, only to discover solid granite, leaving them mystified by ancient Indian acoustical engineering.\n\n[Dynastic Timeline]\n1513 CE: Emperor Krishnadevaraya expands Vittala Temple complex."
                )
            )
        )
    }

    fun getSampleBardicSeals(): List<BardicSeal> {
        return listOf(
            BardicSeal("seal_1", "The Night Siege of Kumbhalgarh", "Bard Ratan Singh Charan", "Kumbhalgarh", 35, "When the moon dipped behind Badal Mahal, forty drums thundered from the mountain pass..."),
            BardicSeal("seal_2", "Song of the Saraswati Stepwell", "Folk Singer Deviben Rabari", "Patan, Gujarat", 42, "Deep beneath seven layers of silt, the nagakanyas still guard Queen Udayamati's water crown..."),
            BardicSeal("seal_3", "Whispers of Gol Gumbaz", "Sufi Qawwal Nizamuddin", "Bijapur", 28, "Seven echoes carry the lover's whisper across 125 feet of vaulted air...")
        )
    }
}
