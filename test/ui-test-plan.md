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
2. `deadline return book /by Sunday`
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
1.[D][X] return book (by: Sunday)
2.[E][ ] project meeting (from: Mon 2pm to: 4pm)
```

Expected `data/noah.txt` after the second run:

```text
D | 0 | return book | Sunday
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
Saving hit a bump! I couldn't save your tasks to <data-file-path>.
Your latest changes are not saved. Please check the file path and permissions.
```

Expected output for `list`:

```text
Quest board, coming right up! Here are your tasks:
1.[T][ ] read book
```

Expected output for `bye` is the farewell block above. The task exists in
memory, but the save failure must be explicit and the directory must remain
a directory.
