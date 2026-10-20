#!/usr/bin/env python3
"""
Animated ASCII cat for the console.
"""

import time
import os
import sys

# Define animation frames for the cat
cat_frames = [
    # Frame 1: Cat at rest, eyes open
    """
    |\\_/|
    (=^.^=)
     "" "" 
    """,

    # Frame 2: Cat blinking
    """
    |\\_/|
    (-^.^-)
     "" "" 
    """,

    # Frame 3: Cat eyes closed (wink)
    """
    |\\_/|
    (=^.~)
     "" "" 
    """,

    # Frame 4: Cat at rest, eyes open again
    """
    |\\_/|
    (=^.^=)
     "" "" 
    """,

    # Frame 5: Cat with raised paw
    """
     /\\_/\\
    (=^.^=)
     "| |" 
    """,

    # Frame 6: Cat back to normal
    """
    |\\_/|
    (=^.^=)
     "" "" 
    """,

    # Frame 7: Cat with tail swish (looking left)
    """
   /|\\_/|
   (=^.^=)
    "" "" 
    """,

    # Frame 8: Back to normal
    """
    |\\_/|
    (=^.^=)
     "" "" 
    """,
]

def clear_screen():
    """Clear the console screen."""
    os.system('clear' if os.name == 'posix' else 'cls')

def print_cat_animation(duration=10, frame_delay_ms=300):
    """
    Print animated cat.

    Args:
        duration: Total duration of animation in seconds (0 = infinite)
        frame_delay_ms: Delay between frames in milliseconds
    """
    frame_delay_sec = frame_delay_ms / 1000.0
    start_time = time.time()
    frame_index = 0

    try:
        while True:
            # Check if we should stop (if duration is specified and not 0)
            if duration > 0 and (time.time() - start_time) > duration:
                break

            # Clear screen and print current frame
            clear_screen()
            print("\n" * 2)  # Add some top margin
            print(cat_frames[frame_index % len(cat_frames)])
            print("\nFrame", (frame_index % len(cat_frames)) + 1, "of", len(cat_frames))

            # Move to next frame
            frame_index += 1

            # Wait before next frame
            time.sleep(frame_delay_sec)

    except KeyboardInterrupt:
        # Allow clean exit with Ctrl+C
        clear_screen()
        print("\n🐱 Cat animation stopped! Goodbye!\n")
        sys.exit(0)

if __name__ == "__main__":
    # Check for command line arguments
    duration = 0  # 0 means infinite
    delay = 300   # 300ms between frames

    if len(sys.argv) > 1:
        try:
            duration = int(sys.argv[1])
        except ValueError:
            print("Usage: animated_cat.py [duration_in_seconds] [delay_in_ms]")
            sys.exit(1)

    if len(sys.argv) > 2:
        try:
            delay = int(sys.argv[2])
        except ValueError:
            print("Usage: animated_cat.py [duration_in_seconds] [delay_in_ms]")
            sys.exit(1)

    print("🐱 Starting cat animation...")
    print("   Press Ctrl+C to stop\n")
    time.sleep(1)

    print_cat_animation(duration=duration, frame_delay_ms=delay)

