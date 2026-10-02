# UI Test Plan

This plan checks Noah's observable command-line behavior. Each standard test
case starts with an empty task list in an isolated working directory and a
fresh process. Expected output blocks correspond one-to-one with the commands
and must appear exactly in the same order.

The banner and separator lines are displayed in the transcript but are omitted
from expected blocks because they do not describe command behavior.

The startup greeting, checked separately in the transcript, is:

```text
Ad astra abyssosque!
Hey, Traveler! I'm Noah, your trusted companion.
Ready for an adventure?
```

Run the complete plan from the repository root:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .codex/skills/test-ui/scripts/run-ui-tests.ps1
```

## Test cases

### List an empty task list and recover after deleting the last task

Aim: Show a clear empty-list message and an example command on a fresh start
and after the last task is deleted. Verify that adding another task works and
that task counts use the correct singular or plural form.

### Add and list all task types

Aim: Verify that todo, deadline, and event commands create correctly formatted
tasks and that `list` preserves their order.

### Mark and unmark an inherited task

Aim: Verify that status changes inherited from `Task` are reflected through
polymorphic deadline output.

### Delete a task and renumber the remaining tasks

Aim: Verify that deleting a numbered task reports the removed task and shifts
later tasks forward in the displayed list.

### Reject invalid delete task numbers

Aim: Verify that delete rejects missing, non-numeric, non-positive, and
out-of-range task numbers without changing the task list.

### Accept tasks without date delimiters

Aim: Verify the intended fallback constructors when deadline or event timing
information is omitted.

### Reject empty todo and unknown commands

Aim: Verify that invalid commands show helpful errors and do not change the
task list.

### Reject invalid task numbers

Aim: Verify that mark and unmark commands reject missing, non-numeric, and
out-of-range task numbers without changing task state.

### Reject invalid deadline and event details

Aim: Verify that deadline and event commands reject empty descriptions and
incomplete date delimiters without changing the task list.

### Preserve command boundaries and reject oversized task numbers

Aim: Reject blank input, command-name prefixes, arguments to `list` and `bye`,
integer overflow, negative task numbers, and reversed event delimiters. A
rejected command must leave tasks unchanged and allow the next command.

### Accept extra spaces in command arguments

Aim: Preserve descriptions, dates, and task numbers when extra spaces separate
the command word, arguments, or date delimiters.

### Accept calendar dates and format them for display

Aim: Parse ISO deadline dates and display English month names. Accept leap
days in 2024 and 2000, and display a single-digit day without a leading zero.

### Validate explicit numeric deadline dates

Aim: Reject impossible or malformed numeric dates and their attached clock times.
Existing tasks must survive rejected commands. Free-text deadline labels remain
supported, including new input using Sunday, June 6th, Mon 2pm, or 2pm.

### Accept both date formats with optional time

Aim: Accept `yyyy-MM-dd` and `d/M/yyyy` (day first), optionally followed by
`HH:mm` or `HHmm` in 24-hour time. Date-only input must not acquire a time.
Formatting, marking, and unmarking must preserve any explicitly supplied time.

### Accept clock boundaries and delete dated tasks

Aim: Accept midnight, 23:59, and extra argument spaces. Delete a timed deadline
and verify the remaining tasks are renumbered correctly.

### Create and manage legacy text deadlines

Aim: Accept the old free-text /by values when creating new tasks, not just when
loading saved tasks. Check listing, mark, unmark, delete, and renumbering.
Keep the first 11 machine-readable cases identical to commit 026a9c9 so the
new formats supplement the original behavior instead of replacing its tests.

### Find descriptions across task types

Aim: Match literal substrings without regard to letter case in todo, deadline,
and event descriptions, including completed tasks. Dates, time labels, and
status/type markers must not create a match by themselves.

### Find with an empty list, no matches, or a missing keyword

Aim: Return a helpful no-match message even on an empty list. Require a
nonblank keyword after the exact find command; findbook and finder remain
unknown commands. Trim surrounding spaces without changing the stored tasks.

### Find literal phrases and punctuation

Aim: Treat the whole query as one literal substring. Queries such as C++, [,
and .* must not be interpreted as regular expressions.

### Keep original task numbers in search results

Aim: Show the same numbers as list, including gaps between results. The user
can mark, unmark, or delete using those numbers. After deletion, search must
use the updated list numbers and leave unrelated tasks untouched.

The first 16 machine-readable cases remain unchanged from Level-8.

## Machine-readable cases

Keep this JSON block synchronized with the descriptions above. The
`test-ui` runner treats it as the executable source of truth.

<!-- TEST-CASES-START -->
```json
[
  {
    "name": "empty-list-and-delete-last-task",
    "aim": "Explain an empty list and allow tasks to be added again after deletion.",
    "commands": [
      "list",
      "todo read book",
      "list",
      "delete 1",
      "list",
      "todo take a break",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "Your task list is empty. No quests on the board!\nReady for one? Try: todo read book",
      "On the board! I've added this task:\n  [T][ ] read book\nNow you have 1 task in the list.",
      "Quest board, coming right up! Here are your tasks:\n1.[T][ ] read book",
      "Off the board! I've removed this task:\n  [T][ ] read book\nNow you have 0 tasks in the list.",
      "Your task list is empty. No quests on the board!\nReady for one? Try: todo read book",
      "On the board! I've added this task:\n  [T][ ] take a break\nNow you have 1 task in the list.",
      "Quest board, coming right up! Here are your tasks:\n1.[T][ ] take a break",
      "Farewell, Traveler!\nTime to recharge. See you on the next quest!"
    ]
  },
  {
    "name": "add-and-list-task-types",
    "aim": "Add and list todo, deadline, and event tasks in order.",
    "commands": [
      "todo borrow book",
      "deadline return book /by Sunday",
      "event project meeting /from Mon 2pm /to 4pm",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "On the board! I've added this task:\n  [T][ ] borrow book\nNow you have 1 task in the list.",
      "On the board! I've added this task:\n  [D][ ] return book (by: Sunday)\nNow you have 2 tasks in the list.",
      "On the board! I've added this task:\n  [E][ ] project meeting (from: Mon 2pm to: 4pm)\nNow you have 3 tasks in the list.",
      "Quest board, coming right up! Here are your tasks:\n1.[T][ ] borrow book\n2.[D][ ] return book (by: Sunday)\n3.[E][ ] project meeting (from: Mon 2pm to: 4pm)",
      "Farewell, Traveler!\nTime to recharge. See you on the next quest!"
    ]
  },
  {
    "name": "mark-and-unmark-deadline",
    "aim": "Update inherited completion state and preserve deadline details.",
    "commands": [
      "deadline return book /by Sunday",
      "mark 1",
      "unmark 1",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "On the board! I've added this task:\n  [D][ ] return book (by: Sunday)\nNow you have 1 task in the list.",
      "One down! I've marked this task as done:\n  [D][X] return book (by: Sunday)",
      "Back on the board! I've marked this task as not done yet:\n  [D][ ] return book (by: Sunday)",
      "Quest board, coming right up! Here are your tasks:\n1.[D][ ] return book (by: Sunday)",
      "Farewell, Traveler!\nTime to recharge. See you on the next quest!"
    ]
  },
  {
    "name": "delete-task-and-renumber",
    "aim": "Delete a numbered task and renumber the remaining tasks.",
    "commands": [
      "todo read book",
      "deadline return book /by Sunday",
      "event project meeting /from Mon 2pm /to 4pm",
      "mark 2",
      "delete 2",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "On the board! I've added this task:\n  [T][ ] read book\nNow you have 1 task in the list.",
      "On the board! I've added this task:\n  [D][ ] return book (by: Sunday)\nNow you have 2 tasks in the list.",
      "On the board! I've added this task:\n  [E][ ] project meeting (from: Mon 2pm to: 4pm)\nNow you have 3 tasks in the list.",
      "One down! I've marked this task as done:\n  [D][X] return book (by: Sunday)",
      "Off the board! I've removed this task:\n  [D][X] return book (by: Sunday)\nNow you have 2 tasks in the list.",
      "Quest board, coming right up! Here are your tasks:\n1.[T][ ] read book\n2.[E][ ] project meeting (from: Mon 2pm to: 4pm)",
      "Farewell, Traveler!\nTime to recharge. See you on the next quest!"
    ]
  },
  {
    "name": "reject-invalid-delete-task-numbers",
    "aim": "Reject invalid delete task numbers without changing the task list.",
    "commands": [
      "delete",
      "delete abc",
      "delete 0",
      "delete 1",
      "todo read book",
      "delete 2",
      "deleteabc",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "Which task, Traveler? Add its number. Try: delete 1",
      "Oops, task numbers start at 1! Use a whole number from list.",
      "Oops, task numbers start at 1! Use a whole number from list.",
      "No task numbered 1 on the board! Type list to check your task numbers.",
      "On the board! I've added this task:\n  [T][ ] read book\nNow you have 1 task in the list.",
      "No task numbered 2 on the board! Type list to check your task numbers.",
      "That command isn't in my adventurer's handbook!\nTry: list, todo read book, or bye.",
      "Quest board, coming right up! Here are your tasks:\n1.[T][ ] read book",
      "Farewell, Traveler!\nTime to recharge. See you on the next quest!"
    ]
  },
  {
    "name": "tasks-without-date-delimiters",
    "aim": "Use fallback constructors when timing delimiters are absent.",
    "commands": [
      "deadline return book",
      "event project meeting",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "On the board! I've added this task:\n  [D][ ] return book\nNow you have 1 task in the list.",
      "On the board! I've added this task:\n  [E][ ] project meeting\nNow you have 2 tasks in the list.",
      "Quest board, coming right up! Here are your tasks:\n1.[D][ ] return book\n2.[E][ ] project meeting",
      "Farewell, Traveler!\nTime to recharge. See you on the next quest!"
    ]
  },
  {
    "name": "reject-empty-todo-and-unknown-command",
    "aim": "Reject invalid commands without changing the task list.",
    "commands": [
      "todo",
      "todo read book",
      "todoabc",
      "blah",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "Every quest needs a name! Add a description. Try: todo read book",
      "On the board! I've added this task:\n  [T][ ] read book\nNow you have 1 task in the list.",
      "That command isn't in my adventurer's handbook!\nTry: list, todo read book, or bye.",
      "That command isn't in my adventurer's handbook!\nTry: list, todo read book, or bye.",
      "Quest board, coming right up! Here are your tasks:\n1.[T][ ] read book",
      "Farewell, Traveler!\nTime to recharge. See you on the next quest!"
    ]
  },
  {
    "name": "reject-invalid-task-numbers",
    "aim": "Reject invalid mark and unmark task numbers without changing task state.",
    "commands": [
      "mark",
      "mark abc",
      "mark 1",
      "todo read book",
      "mark 2",
      "mark 1",
      "unmark 0",
      "unmark 1",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "Which task, Traveler? Add its number. Try: mark 1",
      "Oops, task numbers start at 1! Use a whole number from list.",
      "No task numbered 1 on the board! Type list to check your task numbers.",
      "On the board! I've added this task:\n  [T][ ] read book\nNow you have 1 task in the list.",
      "No task numbered 2 on the board! Type list to check your task numbers.",
      "One down! I've marked this task as done:\n  [T][X] read book",
      "Oops, task numbers start at 1! Use a whole number from list.",
      "Back on the board! I've marked this task as not done yet:\n  [T][ ] read book",
      "Quest board, coming right up! Here are your tasks:\n1.[T][ ] read book",
      "Farewell, Traveler!\nTime to recharge. See you on the next quest!"
    ]
  },
  {
    "name": "reject-invalid-deadline-and-event-details",
    "aim": "Reject incomplete deadline and event details without changing valid tasks.",
    "commands": [
      "deadline",
      "deadline /by Sunday",
      "deadline return book /by",
      "deadline return book",
      "event",
      "event /from Monday /to Tuesday",
      "event project meeting /from Monday",
      "event project meeting /to Tuesday",
      "event project meeting /from Monday /to",
      "event project meeting",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "What's the mission? Add a description. Try: deadline return book /by Sunday",
      "This deadline is missing a piece! Add a description and date.\nTry: deadline return book /by Sunday",
      "This deadline is missing a piece! Add a description and date.\nTry: deadline return book /by Sunday",
      "On the board! I've added this task:\n  [D][ ] return book\nNow you have 1 task in the list.",
      "What's the occasion? Add a description. Try: event meeting /from 2pm /to 4pm",
      "Let's fill in the blanks! An event needs a description, start, and end.\nTry: event meeting /from 2pm /to 4pm",
      "Let's fill in the blanks! An event needs a description, start, and end.\nTry: event meeting /from 2pm /to 4pm",
      "Let's fill in the blanks! An event needs a description, start, and end.\nTry: event meeting /from 2pm /to 4pm",
      "Let's fill in the blanks! An event needs a description, start, and end.\nTry: event meeting /from 2pm /to 4pm",
      "On the board! I've added this task:\n  [E][ ] project meeting\nNow you have 2 tasks in the list.",
      "Quest board, coming right up! Here are your tasks:\n1.[D][ ] return book\n2.[E][ ] project meeting",
      "Farewell, Traveler!\nTime to recharge. See you on the next quest!"
    ]
  },
  {
    "name": "reject-command-boundaries-and-oversized-task-numbers",
    "aim": "Reject malformed commands and oversized task numbers while continuing normally.",
    "commands": [
      "",
      "list extra",
      "bye extra",
      "markabc",
      "todo read book",
      "mark 2147483648",
      "delete -1",
      "event meeting /to Tuesday /from Monday",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "That command isn't in my adventurer's handbook!\nTry: list, todo read book, or bye.",
      "That command isn't in my adventurer's handbook!\nTry: list, todo read book, or bye.",
      "That command isn't in my adventurer's handbook!\nTry: list, todo read book, or bye.",
      "That command isn't in my adventurer's handbook!\nTry: list, todo read book, or bye.",
      "On the board! I've added this task:\n  [T][ ] read book\nNow you have 1 task in the list.",
      "Oops, task numbers start at 1! Use a whole number from list.",
      "Oops, task numbers start at 1! Use a whole number from list.",
      "Let's fill in the blanks! An event needs a description, start, and end.\nTry: event meeting /from 2pm /to 4pm",
      "Quest board, coming right up! Here are your tasks:\n1.[T][ ] read book",
      "Farewell, Traveler!\nTime to recharge. See you on the next quest!"
    ]
  },
  {
    "name": "accept-extra-spaces-in-arguments",
    "aim": "Preserve task data when command arguments contain extra separating spaces.",
    "commands": [
      "todo   read book",
      "deadline   return book   /by   Sunday",
      "event   meeting   /from   Monday   /to   Tuesday",
      "mark   2",
      "unmark   2",
      "delete   1",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "On the board! I've added this task:\n  [T][ ] read book\nNow you have 1 task in the list.",
      "On the board! I've added this task:\n  [D][ ] return book (by: Sunday)\nNow you have 2 tasks in the list.",
      "On the board! I've added this task:\n  [E][ ] meeting (from: Monday to: Tuesday)\nNow you have 3 tasks in the list.",
      "One down! I've marked this task as done:\n  [D][X] return book (by: Sunday)",
      "Back on the board! I've marked this task as not done yet:\n  [D][ ] return book (by: Sunday)",
      "Off the board! I've removed this task:\n  [T][ ] read book\nNow you have 2 tasks in the list.",
      "Quest board, coming right up! Here are your tasks:\n1.[D][ ] return book (by: Sunday)\n2.[E][ ] meeting (from: Monday to: Tuesday)",
      "Farewell, Traveler!\nTime to recharge. See you on the next quest!"
    ]
  },
  {
    "name": "accept-calendar-dates-and-format-for-display",
    "aim": "Parse ISO dates, accept leap days, and display English month names.",
    "commands": [
      "deadline return book /by 2019-10-15",
      "deadline leap day /by 2024-02-29",
      "deadline century leap day /by 2000-02-29",
      "deadline new year /by 2026-01-01",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "On the board! I've added this task:\n  [D][ ] return book (by: Oct 15 2019)\nNow you have 1 task in the list.",
      "On the board! I've added this task:\n  [D][ ] leap day (by: Feb 29 2024)\nNow you have 2 tasks in the list.",
      "On the board! I've added this task:\n  [D][ ] century leap day (by: Feb 29 2000)\nNow you have 3 tasks in the list.",
      "On the board! I've added this task:\n  [D][ ] new year (by: Jan 1 2026)\nNow you have 4 tasks in the list.",
      "Quest board, coming right up! Here are your tasks:\n1.[D][ ] return book (by: Oct 15 2019)\n2.[D][ ] leap day (by: Feb 29 2024)\n3.[D][ ] century leap day (by: Feb 29 2000)\n4.[D][ ] new year (by: Jan 1 2026)",
      "Farewell, Traveler!\nTime to recharge. See you on the next quest!"
    ]
  },
  {
    "name": "reject-invalid-deadline-dates",
    "aim": "Validate explicit numeric dates and their times while preserving the existing task list.",
    "commands": [
      "deadline keep me /by 2026-10-04",
      "deadline invalid date /by 2026-02-29",
      "deadline invalid date /by 2024-02-30",
      "deadline invalid date /by 1900-02-29",
      "deadline invalid date /by 2026-04-31",
      "deadline invalid date /by 2026-13-01",
      "deadline invalid date /by 2026-00-10",
      "deadline invalid date /by 2026-01-00",
      "deadline invalid date /by 2026-2-3",
      "deadline invalid date /by 31/4/2026",
      "deadline invalid date /by 29/2/1900",
      "deadline invalid date /by 12/31/2019",
      "deadline invalid date /by 0000-01-01",
      "deadline invalid date /by 2019-12-02 24:00",
      "deadline invalid date /by 2/12/2019 1260",
      "deadline invalid date /by 2019-12-02 180",
      "deadline invalid date /by 2019-12-02 18:0",
      "deadline invalid date /by 2019-12-02 18:00:00",
      "deadline invalid date /by 2019-12-02 6pm",
      "deadline invalid date /by 2019-12-02 evening",
      "deadline invalid date /by 2019-12-02 1800 extra",
      "list",
      "deadline next quest /by 31/12/2026",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "On the board! I've added this task:\n  [D][ ] keep me (by: Oct 4 2026)\nNow you have 1 task in the list.",
      "That date needs a second look! Use yyyy-MM-dd or d/M/yyyy.\nTime is optional: add HH:mm or HHmm, like 2019-12-02 18:00.",
      "That date needs a second look! Use yyyy-MM-dd or d/M/yyyy.\nTime is optional: add HH:mm or HHmm, like 2019-12-02 18:00.",
      "That date needs a second look! Use yyyy-MM-dd or d/M/yyyy.\nTime is optional: add HH:mm or HHmm, like 2019-12-02 18:00.",
      "That date needs a second look! Use yyyy-MM-dd or d/M/yyyy.\nTime is optional: add HH:mm or HHmm, like 2019-12-02 18:00.",
      "That date needs a second look! Use yyyy-MM-dd or d/M/yyyy.\nTime is optional: add HH:mm or HHmm, like 2019-12-02 18:00.",
      "That date needs a second look! Use yyyy-MM-dd or d/M/yyyy.\nTime is optional: add HH:mm or HHmm, like 2019-12-02 18:00.",
      "That date needs a second look! Use yyyy-MM-dd or d/M/yyyy.\nTime is optional: add HH:mm or HHmm, like 2019-12-02 18:00.",
      "That date needs a second look! Use yyyy-MM-dd or d/M/yyyy.\nTime is optional: add HH:mm or HHmm, like 2019-12-02 18:00.",
      "That date needs a second look! Use yyyy-MM-dd or d/M/yyyy.\nTime is optional: add HH:mm or HHmm, like 2019-12-02 18:00.",
      "That date needs a second look! Use yyyy-MM-dd or d/M/yyyy.\nTime is optional: add HH:mm or HHmm, like 2019-12-02 18:00.",
      "That date needs a second look! Use yyyy-MM-dd or d/M/yyyy.\nTime is optional: add HH:mm or HHmm, like 2019-12-02 18:00.",
      "That date needs a second look! Use yyyy-MM-dd or d/M/yyyy.\nTime is optional: add HH:mm or HHmm, like 2019-12-02 18:00.",
      "That date needs a second look! Use yyyy-MM-dd or d/M/yyyy.\nTime is optional: add HH:mm or HHmm, like 2019-12-02 18:00.",
      "That date needs a second look! Use yyyy-MM-dd or d/M/yyyy.\nTime is optional: add HH:mm or HHmm, like 2019-12-02 18:00.",
      "That date needs a second look! Use yyyy-MM-dd or d/M/yyyy.\nTime is optional: add HH:mm or HHmm, like 2019-12-02 18:00.",
      "That date needs a second look! Use yyyy-MM-dd or d/M/yyyy.\nTime is optional: add HH:mm or HHmm, like 2019-12-02 18:00.",
      "That date needs a second look! Use yyyy-MM-dd or d/M/yyyy.\nTime is optional: add HH:mm or HHmm, like 2019-12-02 18:00.",
      "That date needs a second look! Use yyyy-MM-dd or d/M/yyyy.\nTime is optional: add HH:mm or HHmm, like 2019-12-02 18:00.",
      "That date needs a second look! Use yyyy-MM-dd or d/M/yyyy.\nTime is optional: add HH:mm or HHmm, like 2019-12-02 18:00.",
      "That date needs a second look! Use yyyy-MM-dd or d/M/yyyy.\nTime is optional: add HH:mm or HHmm, like 2019-12-02 18:00.",
      "Quest board, coming right up! Here are your tasks:\n1.[D][ ] keep me (by: Oct 4 2026)",
      "On the board! I've added this task:\n  [D][ ] next quest (by: Dec 31 2026)\nNow you have 2 tasks in the list.",
      "Quest board, coming right up! Here are your tasks:\n1.[D][ ] keep me (by: Oct 4 2026)\n2.[D][ ] next quest (by: Dec 31 2026)",
      "Farewell, Traveler!\nTime to recharge. See you on the next quest!"
    ]
  },
  {
    "name": "accept-both-date-formats-with-optional-time",
    "aim": "Accept ISO and day-first dates with or without a 24-hour clock time; preserve time on status updates.",
    "commands": [
      "deadline ISO date /by 2019-12-02",
      "deadline slash date /by 2/12/2019",
      "deadline ISO time /by 2019-12-02 1800",
      "deadline slash time /by 2/12/2019 1800",
      "deadline ISO colon /by 2019-12-02 18:30",
      "deadline slash colon /by 02/12/2019 18:30",
      "deadline slash leap day /by 29/2/2000",
      "list",
      "mark 4",
      "unmark 4",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "On the board! I've added this task:\n  [D][ ] ISO date (by: Dec 2 2019)\nNow you have 1 task in the list.",
      "On the board! I've added this task:\n  [D][ ] slash date (by: Dec 2 2019)\nNow you have 2 tasks in the list.",
      "On the board! I've added this task:\n  [D][ ] ISO time (by: Dec 2 2019 18:00)\nNow you have 3 tasks in the list.",
      "On the board! I've added this task:\n  [D][ ] slash time (by: Dec 2 2019 18:00)\nNow you have 4 tasks in the list.",
      "On the board! I've added this task:\n  [D][ ] ISO colon (by: Dec 2 2019 18:30)\nNow you have 5 tasks in the list.",
      "On the board! I've added this task:\n  [D][ ] slash colon (by: Dec 2 2019 18:30)\nNow you have 6 tasks in the list.",
      "On the board! I've added this task:\n  [D][ ] slash leap day (by: Feb 29 2000)\nNow you have 7 tasks in the list.",
      "Quest board, coming right up! Here are your tasks:\n1.[D][ ] ISO date (by: Dec 2 2019)\n2.[D][ ] slash date (by: Dec 2 2019)\n3.[D][ ] ISO time (by: Dec 2 2019 18:00)\n4.[D][ ] slash time (by: Dec 2 2019 18:00)\n5.[D][ ] ISO colon (by: Dec 2 2019 18:30)\n6.[D][ ] slash colon (by: Dec 2 2019 18:30)\n7.[D][ ] slash leap day (by: Feb 29 2000)",
      "One down! I've marked this task as done:\n  [D][X] slash time (by: Dec 2 2019 18:00)",
      "Back on the board! I've marked this task as not done yet:\n  [D][ ] slash time (by: Dec 2 2019 18:00)",
      "Quest board, coming right up! Here are your tasks:\n1.[D][ ] ISO date (by: Dec 2 2019)\n2.[D][ ] slash date (by: Dec 2 2019)\n3.[D][ ] ISO time (by: Dec 2 2019 18:00)\n4.[D][ ] slash time (by: Dec 2 2019 18:00)\n5.[D][ ] ISO colon (by: Dec 2 2019 18:30)\n6.[D][ ] slash colon (by: Dec 2 2019 18:30)\n7.[D][ ] slash leap day (by: Feb 29 2000)",
      "Farewell, Traveler!\nTime to recharge. See you on the next quest!"
    ]
  },
  {
    "name": "accept-clock-boundaries-and-delete-dated-task",
    "aim": "Accept valid day boundaries and extra spaces, then delete and renumber a timed deadline.",
    "commands": [
      "deadline midnight /by 2026-10-04 0000",
      "deadline last minute /by 4/10/2026 23:59",
      "deadline extra spaces /by   2026-10-04   18:00  ",
      "list",
      "delete 2",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "On the board! I've added this task:\n  [D][ ] midnight (by: Oct 4 2026 00:00)\nNow you have 1 task in the list.",
      "On the board! I've added this task:\n  [D][ ] last minute (by: Oct 4 2026 23:59)\nNow you have 2 tasks in the list.",
      "On the board! I've added this task:\n  [D][ ] extra spaces (by: Oct 4 2026 18:00)\nNow you have 3 tasks in the list.",
      "Quest board, coming right up! Here are your tasks:\n1.[D][ ] midnight (by: Oct 4 2026 00:00)\n2.[D][ ] last minute (by: Oct 4 2026 23:59)\n3.[D][ ] extra spaces (by: Oct 4 2026 18:00)",
      "Off the board! I've removed this task:\n  [D][ ] last minute (by: Oct 4 2026 23:59)\nNow you have 2 tasks in the list.",
      "Quest board, coming right up! Here are your tasks:\n1.[D][ ] midnight (by: Oct 4 2026 00:00)\n2.[D][ ] extra spaces (by: Oct 4 2026 18:00)",
      "Farewell, Traveler!\nTime to recharge. See you on the next quest!"
    ]
  },
  {
    "name": "create-and-manage-legacy-text-deadlines",
    "aim": "Keep old free-text deadline creation, listing, marking, unmarking, and deletion usable.",
    "commands": [
      "deadline old format 1 /by Sunday",
      "deadline old format 2 /by Mon 2pm",
      "deadline old format 3 /by 2pm",
      "deadline old format 4 /by June 6th",
      "deadline old format 5 /by tomorrow",
      "deadline old format 6 /by next week",
      "deadline old format 7 /by after dinner",
      "deadline old format 8 /by October",
      "deadline old format 9 /by no idea :-p",
      "list",
      "mark 2",
      "list",
      "unmark 2",
      "delete 1",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "On the board! I've added this task:\n  [D][ ] old format 1 (by: Sunday)\nNow you have 1 task in the list.",
      "On the board! I've added this task:\n  [D][ ] old format 2 (by: Mon 2pm)\nNow you have 2 tasks in the list.",
      "On the board! I've added this task:\n  [D][ ] old format 3 (by: 2pm)\nNow you have 3 tasks in the list.",
      "On the board! I've added this task:\n  [D][ ] old format 4 (by: June 6th)\nNow you have 4 tasks in the list.",
      "On the board! I've added this task:\n  [D][ ] old format 5 (by: tomorrow)\nNow you have 5 tasks in the list.",
      "On the board! I've added this task:\n  [D][ ] old format 6 (by: next week)\nNow you have 6 tasks in the list.",
      "On the board! I've added this task:\n  [D][ ] old format 7 (by: after dinner)\nNow you have 7 tasks in the list.",
      "On the board! I've added this task:\n  [D][ ] old format 8 (by: October)\nNow you have 8 tasks in the list.",
      "On the board! I've added this task:\n  [D][ ] old format 9 (by: no idea :-p)\nNow you have 9 tasks in the list.",
      "Quest board, coming right up! Here are your tasks:\n1.[D][ ] old format 1 (by: Sunday)\n2.[D][ ] old format 2 (by: Mon 2pm)\n3.[D][ ] old format 3 (by: 2pm)\n4.[D][ ] old format 4 (by: June 6th)\n5.[D][ ] old format 5 (by: tomorrow)\n6.[D][ ] old format 6 (by: next week)\n7.[D][ ] old format 7 (by: after dinner)\n8.[D][ ] old format 8 (by: October)\n9.[D][ ] old format 9 (by: no idea :-p)",
      "One down! I've marked this task as done:\n  [D][X] old format 2 (by: Mon 2pm)",
      "Quest board, coming right up! Here are your tasks:\n1.[D][ ] old format 1 (by: Sunday)\n2.[D][X] old format 2 (by: Mon 2pm)\n3.[D][ ] old format 3 (by: 2pm)\n4.[D][ ] old format 4 (by: June 6th)\n5.[D][ ] old format 5 (by: tomorrow)\n6.[D][ ] old format 6 (by: next week)\n7.[D][ ] old format 7 (by: after dinner)\n8.[D][ ] old format 8 (by: October)\n9.[D][ ] old format 9 (by: no idea :-p)",
      "Back on the board! I've marked this task as not done yet:\n  [D][ ] old format 2 (by: Mon 2pm)",
      "Off the board! I've removed this task:\n  [D][ ] old format 1 (by: Sunday)\nNow you have 8 tasks in the list.",
      "Quest board, coming right up! Here are your tasks:\n1.[D][ ] old format 2 (by: Mon 2pm)\n2.[D][ ] old format 3 (by: 2pm)\n3.[D][ ] old format 4 (by: June 6th)\n4.[D][ ] old format 5 (by: tomorrow)\n5.[D][ ] old format 6 (by: next week)\n6.[D][ ] old format 7 (by: after dinner)\n7.[D][ ] old format 8 (by: October)\n8.[D][ ] old format 9 (by: no idea :-p)",
      "Farewell, Traveler!\nTime to recharge. See you on the next quest!"
    ]
  },
  {
    "name": "find-all-task-types-and-descriptions-only",
    "aim": "Find case-insensitive substrings in all task descriptions, including completed tasks, but not metadata.",
    "commands": [
      "todo buy milk",
      "todo Read BOOK",
      "deadline return book /by 2/12/2019 1800",
      "deadline pay rent /by Sunday",
      "event book club /from Mon 2pm /to 4pm",
      "todo notebook",
      "mark 3",
      "find book",
      "find BOOK",
      "find Sunday",
      "find Mon 2pm",
      "find Dec 2 2019",
      "find [X]",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "On the board! I've added this task:\n  [T][ ] buy milk\nNow you have 1 task in the list.",
      "On the board! I've added this task:\n  [T][ ] Read BOOK\nNow you have 2 tasks in the list.",
      "On the board! I've added this task:\n  [D][ ] return book (by: Dec 2 2019 18:00)\nNow you have 3 tasks in the list.",
      "On the board! I've added this task:\n  [D][ ] pay rent (by: Sunday)\nNow you have 4 tasks in the list.",
      "On the board! I've added this task:\n  [E][ ] book club (from: Mon 2pm to: 4pm)\nNow you have 5 tasks in the list.",
      "On the board! I've added this task:\n  [T][ ] notebook\nNow you have 6 tasks in the list.",
      "One down! I've marked this task as done:\n  [D][X] return book (by: Dec 2 2019 18:00)",
      "Quest search complete! Here are your matching tasks:\nTask numbers are the same as in list.\n2.[T][ ] Read BOOK\n3.[D][X] return book (by: Dec 2 2019 18:00)\n5.[E][ ] book club (from: Mon 2pm to: 4pm)\n6.[T][ ] notebook",
      "Quest search complete! Here are your matching tasks:\nTask numbers are the same as in list.\n2.[T][ ] Read BOOK\n3.[D][X] return book (by: Dec 2 2019 18:00)\n5.[E][ ] book club (from: Mon 2pm to: 4pm)\n6.[T][ ] notebook",
      "No matching quests this time. Try another keyword!",
      "No matching quests this time. Try another keyword!",
      "No matching quests this time. Try another keyword!",
      "No matching quests this time. Try another keyword!",
      "Quest board, coming right up! Here are your tasks:\n1.[T][ ] buy milk\n2.[T][ ] Read BOOK\n3.[D][X] return book (by: Dec 2 2019 18:00)\n4.[D][ ] pay rent (by: Sunday)\n5.[E][ ] book club (from: Mon 2pm to: 4pm)\n6.[T][ ] notebook",
      "Farewell, Traveler!\nTime to recharge. See you on the next quest!"
    ]
  },
  {
    "name": "find-empty-list-and-no-matches",
    "aim": "Always respond to searches, including on an empty list, and leave tasks unchanged.",
    "commands": [
      "find book",
      "list",
      "todo read book",
      "find unicorn",
      "find book",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "No matching quests this time. Try another keyword!",
      "Your task list is empty. No quests on the board!\nReady for one? Try: todo read book",
      "On the board! I've added this task:\n  [T][ ] read book\nNow you have 1 task in the list.",
      "No matching quests this time. Try another keyword!",
      "Quest search complete! Here are your matching tasks:\nTask numbers are the same as in list.\n1.[T][ ] read book",
      "Quest board, coming right up! Here are your tasks:\n1.[T][ ] read book",
      "Farewell, Traveler!\nTime to recharge. See you on the next quest!"
    ]
  },
  {
    "name": "find-requires-keyword-and-exact-command",
    "aim": "Reject a missing or blank keyword and command prefixes, then accept a correctly spaced search.",
    "commands": [
      "todo read book",
      "find",
      "find   ",
      "findbook",
      "finder book",
      "find   BOOK   ",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "On the board! I've added this task:\n  [T][ ] read book\nNow you have 1 task in the list.",
      "What are we looking for, Traveler? Add a keyword. Try: find book",
      "What are we looking for, Traveler? Add a keyword. Try: find book",
      "That command isn't in my adventurer's handbook!\nTry: list, todo read book, or bye.",
      "That command isn't in my adventurer's handbook!\nTry: list, todo read book, or bye.",
      "Quest search complete! Here are your matching tasks:\nTask numbers are the same as in list.\n1.[T][ ] read book",
      "Quest board, coming right up! Here are your tasks:\n1.[T][ ] read book",
      "Farewell, Traveler!\nTime to recharge. See you on the next quest!"
    ]
  },
  {
    "name": "find-literal-phrases-and-punctuation",
    "aim": "Treat the entire trimmed query as literal text, with no regex or multiple-keyword interpretation.",
    "commands": [
      "todo plan read book club",
      "todo read notebook",
      "todo C++ practice",
      "todo check [draft]",
      "todo try .* literally",
      "find   read book   ",
      "find C++",
      "find [",
      "find .*",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "On the board! I've added this task:\n  [T][ ] plan read book club\nNow you have 1 task in the list.",
      "On the board! I've added this task:\n  [T][ ] read notebook\nNow you have 2 tasks in the list.",
      "On the board! I've added this task:\n  [T][ ] C++ practice\nNow you have 3 tasks in the list.",
      "On the board! I've added this task:\n  [T][ ] check [draft]\nNow you have 4 tasks in the list.",
      "On the board! I've added this task:\n  [T][ ] try .* literally\nNow you have 5 tasks in the list.",
      "Quest search complete! Here are your matching tasks:\nTask numbers are the same as in list.\n1.[T][ ] plan read book club",
      "Quest search complete! Here are your matching tasks:\nTask numbers are the same as in list.\n3.[T][ ] C++ practice",
      "Quest search complete! Here are your matching tasks:\nTask numbers are the same as in list.\n4.[T][ ] check [draft]",
      "Quest search complete! Here are your matching tasks:\nTask numbers are the same as in list.\n5.[T][ ] try .* literally",
      "Quest board, coming right up! Here are your tasks:\n1.[T][ ] plan read book club\n2.[T][ ] read notebook\n3.[T][ ] C++ practice\n4.[T][ ] check [draft]\n5.[T][ ] try .* literally",
      "Farewell, Traveler!\nTime to recharge. See you on the next quest!"
    ]
  },
  {
    "name": "find-keeps-original-numbers-through-edits",
    "aim": "Use displayed search numbers with mark, unmark, and delete without changing unrelated tasks.",
    "commands": [
      "todo buy milk",
      "todo read book",
      "todo buy bread",
      "deadline return book /by Sunday",
      "find book",
      "mark 4",
      "find book",
      "delete 2",
      "find book",
      "unmark 3",
      "find book",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "On the board! I've added this task:\n  [T][ ] buy milk\nNow you have 1 task in the list.",
      "On the board! I've added this task:\n  [T][ ] read book\nNow you have 2 tasks in the list.",
      "On the board! I've added this task:\n  [T][ ] buy bread\nNow you have 3 tasks in the list.",
      "On the board! I've added this task:\n  [D][ ] return book (by: Sunday)\nNow you have 4 tasks in the list.",
      "Quest search complete! Here are your matching tasks:\nTask numbers are the same as in list.\n2.[T][ ] read book\n4.[D][ ] return book (by: Sunday)",
      "One down! I've marked this task as done:\n  [D][X] return book (by: Sunday)",
      "Quest search complete! Here are your matching tasks:\nTask numbers are the same as in list.\n2.[T][ ] read book\n4.[D][X] return book (by: Sunday)",
      "Off the board! I've removed this task:\n  [T][ ] read book\nNow you have 3 tasks in the list.",
      "Quest search complete! Here are your matching tasks:\nTask numbers are the same as in list.\n3.[D][X] return book (by: Sunday)",
      "Back on the board! I've marked this task as not done yet:\n  [D][ ] return book (by: Sunday)",
      "Quest search complete! Here are your matching tasks:\nTask numbers are the same as in list.\n3.[D][ ] return book (by: Sunday)",
      "Quest board, coming right up! Here are your tasks:\n1.[T][ ] buy milk\n2.[T][ ] buy bread\n3.[D][ ] return book (by: Sunday)",
      "Farewell, Traveler!\nTime to recharge. See you on the next quest!"
    ]
  }
]
```
<!-- TEST-CASES-END -->

## Cross-run persistence case

### Save and load all task types

Aim: Verify that todo, deadline, and event tasks, including completion state,
are saved after changes and restored when Noah starts again in the same working
directory. Also verify that a missing `data` directory and `noah.txt` file are
created automatically.

First run commands:

1. `todo borrow book`
2. `deadline return book /by 2026-10-04`
3. `event project meeting /from Mon 2pm /to 4pm`
4. `mark 2`
5. `delete 1`
6. `bye`

Second run commands in the same working directory:

1. `list`
2. `unmark 1`
3. `delete 2`
4. `bye`

Expected list after restarting:

```text
Quest board, coming right up! Here are your tasks:
1.[D][X] return book (by: Oct 4 2026)
2.[E][ ] project meeting (from: Mon 2pm to: 4pm)
```

Expected `data/noah.txt` after the second run:

```text
D | 0 | return book | 2026-10-04
```

Run both launches from one temporary working directory. This case is separate
from the machine-readable block because it requires two Noah processes to
share the same data file.

## Ad hoc stress case

### Accept tasks beyond the old fixed capacity

Aim: Verify that Noah accepts 101 tasks and reports 101 tasks in the list after
the final addition, demonstrating that task storage is no longer limited by a
fixed-size array.

Run this case with the `test-ui` runner's ad hoc parameters. It is kept out of
the JSON block because listing 100 identical setup commands would make the
default test plan unnecessarily repetitive.

## Storage error message checks

Use isolated working directories for these checks. In the output below,
`<data-file-path>` is the platform-specific form of `data/noah.txt`.

### Invalid saved data

Put `not a task` on the first line of the data file, then run `list` and `bye`.
Startup must report:

```text
Oops, I couldn't load your saved tasks: line 1 in <data-file-path> is invalid.
Please fix that line and restart Noah.
```

Expected output for `list`:

```text
Your task list is empty. No quests on the board!
Ready for one? Try: todo read book
```

Expected output for `bye`:

```text
Farewell, Traveler!
Time to recharge. See you on the next quest!
```

The invalid data file must remain unchanged when only `list` and `bye` are used.

### Data-file path is a directory

Create a directory at `data/noah.txt`, then run `todo read book`, `list`, and
`bye`. Startup must report:

```text
Oops, I couldn't load your saved tasks from <data-file-path>.
Please check the file path and permissions, then restart Noah.
```

Expected output for `todo read book`:

```text
On the board! I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
Your saved tasks need a little rescue first!
Your latest changes are only in memory. Fix the loading error and restart Noah before saving.
```

Expected output for `list`:

```text
Quest board, coming right up! Here are your tasks:
1.[T][ ] read book
```

Expected output for `bye` is the farewell block above. The task exists in
memory, but the save failure must be explicit and the directory must remain
a directory.

## Deadline storage and locale checks

### Preserve dates from older saved files

Older versions accepted arbitrary date strings. Load a file containing
`Sunday`, `June 6th`, and an old impossible value such as `2026-02-29` as
deadline fields. List them unchanged, add a valid dated task, mark an old task,
and restart. Preserve the legacy text and completion state throughout.
New `/by` input must also accept free-text labels. Numeric dates and their
attached times are validated, while labels are retained without guessing a date.

### Round-trip both numeric date formats and optional times

Add ISO and slash dates, with and without times. Verify that stored values
use ISO dates and optional `HH:mm`, then restart and check the display and
completion state. Also load an old numeric field `2/12/2019 1800` directly;
it must display `Dec 2 2019 18:00` and be saved as `2019-12-02 18:00`.
An undated deadline must still load and save without a fourth field.

### Preserve a malformed saved file and recover after repair

Create `data/noah.txt` containing `not a task`. Attempt to add a task after
the startup error. Show the save-blocked message from the directory-path
case above and leave the original bytes unchanged. Replace the malformed
record with `D | 1 | return book | 2026-10-04`, restart, and unmark the task.
Loading and saving must now work, with the deadline stored using status `0`.

### Use English month names regardless of the system locale

Start Noah in a fresh working directory with Java options
`-Duser.language=zh -Duser.country=CN`. Add a deadline for `2019-10-15 18:00`,
list it, and exit. The display must be `Oct 15 2019 18:00`; the saved value
must be `2019-10-15 18:00`.


### Round-trip newly entered text deadlines

In an empty working directory, create deadlines with `Sunday`, `June 6th`,
`Mon 2pm`, `2pm`, and a custom text label. Also create an event with
`/from Mon 2pm /to 4pm`, a todo, and both numeric deadline formats. Mark a
text deadline, restart, list everything, unmark it, delete a different text
deadline, and restart again. The text, task order, and completion states
must survive every save and reload.


## Find persistence checks

### Search loaded tasks without saving

Load a data file containing a todo, completed task, legacy date label, and a
numeric deadline saved as 2/12/2019 1800. Run matching and nonmatching searches,
a missing-keyword command, list, and bye. The saved file's exact bytes and
modification time must stay unchanged. Repeat a search in a Turkish locale
to ensure case-insensitive matching does not depend on the system language.

### Edit found tasks and search after restarting

Use original search numbers to mark a matching task and delete another.
Restart and verify descriptions, statuses, order, and renumbered search
results. Tasks excluded from the search must remain in the saved list.
