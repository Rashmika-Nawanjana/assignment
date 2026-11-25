"""Generate formal submission PDFs for Sun Travels ERP."""
from __future__ import annotations

from pathlib import Path

from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_JUSTIFY, TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import inch, mm
from reportlab.platypus import (
    Image,
    KeepTogether,
    ListFlowable,
    ListItem,
    PageBreak,
    Paragraph,
    SimpleDocTemplate,
    Spacer,
    Table,
    TableStyle,
)

ROOT = Path(__file__).resolve().parent
OUT = ROOT / "submission-pdfs"
SHOTS = ROOT / "manual-screenshots"
OUT.mkdir(parents=True, exist_ok=True)

TEAL = colors.HexColor("#0f6e6a")
NAVY = colors.HexColor("#143848")
LIGHT = colors.HexColor("#e8f1f4")
LINE = colors.HexColor("#c9d5dc")
WHITE = colors.white


def styles():
    base = getSampleStyleSheet()
    return {
        "cover_title": ParagraphStyle(
            "cover_title",
            parent=base["Title"],
            fontName="Helvetica-Bold",
            fontSize=22,
            textColor=NAVY,
            alignment=TA_CENTER,
            spaceAfter=8,
        ),
        "cover_sub": ParagraphStyle(
            "cover_sub",
            parent=base["Normal"],
            fontName="Helvetica",
            fontSize=12,
            textColor=TEAL,
            alignment=TA_CENTER,
            spaceAfter=6,
        ),
        "meta": ParagraphStyle(
            "meta",
            parent=base["Normal"],
            fontName="Helvetica",
            fontSize=10,
            textColor=colors.HexColor("#5b6b76"),
            alignment=TA_CENTER,
            spaceAfter=4,
        ),
        "h1": ParagraphStyle(
            "h1",
            parent=base["Heading1"],
            fontName="Helvetica-Bold",
            fontSize=14,
            textColor=NAVY,
            spaceBefore=14,
            spaceAfter=8,
        ),
        "h2": ParagraphStyle(
            "h2",
            parent=base["Heading2"],
            fontName="Helvetica-Bold",
            fontSize=12,
            textColor=TEAL,
            spaceBefore=10,
            spaceAfter=6,
        ),
        "h3": ParagraphStyle(
            "h3",
            parent=base["Heading3"],
            fontName="Helvetica-Bold",
            fontSize=11,
            textColor=NAVY,
            spaceBefore=8,
            spaceAfter=4,
        ),
        "body": ParagraphStyle(
            "body",
            parent=base["Normal"],
            fontName="Helvetica",
            fontSize=10,
            leading=14,
            alignment=TA_JUSTIFY,
            spaceAfter=6,
        ),
        "bullet": ParagraphStyle(
            "bullet",
            parent=base["Normal"],
            fontName="Helvetica",
            fontSize=10,
            leading=13,
            leftIndent=8,
        ),
        "caption": ParagraphStyle(
            "caption",
            parent=base["Normal"],
            fontName="Helvetica-Oblique",
            fontSize=9,
            textColor=colors.HexColor("#5b6b76"),
            alignment=TA_CENTER,
            spaceBefore=4,
            spaceAfter=10,
        ),
        "table_cell": ParagraphStyle(
            "table_cell",
            parent=base["Normal"],
            fontName="Helvetica",
            fontSize=8,
            leading=11,
        ),
        "table_header": ParagraphStyle(
            "table_header",
            parent=base["Normal"],
            fontName="Helvetica-Bold",
            fontSize=8,
            leading=11,
            textColor=WHITE,
        ),
        "story_as": ParagraphStyle(
            "story_as",
            parent=base["Normal"],
            fontName="Helvetica-Oblique",
            fontSize=10,
            leading=13,
            textColor=NAVY,
            spaceAfter=3,
        ),
    }


def header_footer(canvas, doc):
    canvas.saveState()
    canvas.setStrokeColor(LINE)
    canvas.setLineWidth(0.5)
    canvas.line(20 * mm, A4[1] - 15 * mm, A4[0] - 20 * mm, A4[1] - 15 * mm)
    canvas.setFont("Helvetica", 8)
    canvas.setFillColor(TEAL)
    canvas.drawString(20 * mm, A4[1] - 12 * mm, "Sun Travels ERP — Training Assignment Submission")
    canvas.line(20 * mm, 14 * mm, A4[0] - 20 * mm, 14 * mm)
    canvas.setFillColor(colors.HexColor("#5b6b76"))
    canvas.drawRightString(A4[0] - 20 * mm, 10 * mm, f"Page {doc.page}")
    canvas.restoreState()


def cover(story, S, title: str, subtitle: str):
    story.append(Spacer(1, 40 * mm))
    story.append(Paragraph("SUN TRAVELS", S["cover_sub"]))
    story.append(Paragraph("Operations ERP", S["meta"]))
    story.append(Spacer(1, 10 * mm))
    story.append(Paragraph(title, S["cover_title"]))
    story.append(Paragraph(subtitle, S["cover_sub"]))
    story.append(Spacer(1, 12 * mm))
    story.append(Paragraph("Technical Training Assignment — 2026", S["meta"]))
    story.append(Paragraph("Spring Boot · Angular · MySQL", S["meta"]))
    story.append(PageBreak())


def bullets(items, S):
    return ListFlowable(
        [ListItem(Paragraph(i, S["bullet"]), leftIndent=12, bulletColor=TEAL) for i in items],
        bulletType="bullet",
        start="•",
        leftIndent=15,
        bulletFontSize=10,
    )


def formal_table(headers, rows, col_widths, S):
    data = [[Paragraph(h, S["table_header"]) for h in headers]]
    for row in rows:
        data.append([Paragraph(str(c), S["table_cell"]) for c in row])
    table = Table(data, colWidths=col_widths, repeatRows=1)
    table.setStyle(
        TableStyle(
            [
                ("BACKGROUND", (0, 0), (-1, 0), TEAL),
                ("TEXTCOLOR", (0, 0), (-1, 0), WHITE),
                ("BACKGROUND", (0, 1), (-1, -1), WHITE),
                ("ROWBACKGROUNDS", (0, 1), (-1, -1), [WHITE, LIGHT]),
                ("GRID", (0, 0), (-1, -1), 0.4, LINE),
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
                ("LEFTPADDING", (0, 0), (-1, -1), 4),
                ("RIGHTPADDING", (0, 0), (-1, -1), 4),
                ("TOPPADDING", (0, 0), (-1, -1), 4),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 4),
            ]
        )
    )
    return table


def shot(path: Path, caption: str, S, max_width=160 * mm, max_height=90 * mm):
    if not path.exists():
        return [Paragraph(f"[Screenshot unavailable: {path.name}]", S["caption"])]
    img = Image(str(path))
    img.hAlign = "CENTER"
    tw, th = img.imageWidth, img.imageHeight
    scale = min(max_width / tw, max_height / th, 1.0)
    img.drawWidth = tw * scale
    img.drawHeight = th * scale
    return KeepTogether([img, Paragraph(caption, S["caption"])])


def build_user_stories():
    S = styles()
    path = OUT / "SunTravels-User-Stories.pdf"
    doc = SimpleDocTemplate(
        str(path),
        pagesize=A4,
        leftMargin=20 * mm,
        rightMargin=20 * mm,
        topMargin=22 * mm,
        bottomMargin=20 * mm,
        title="Sun Travels ERP — User Stories",
        author="Sun Travels Training Assignment",
    )
    story = []
    cover(story, S, "User Stories", "Functional requirements expressed as user stories with acceptance criteria")

    story.append(Paragraph("1. Introduction", S["h1"]))
    story.append(
        Paragraph(
            "This document presents the user stories that define the Sun Travels ERP solution. "
            "Each story follows the format <b>As a … I want … so that …</b> and includes "
            "acceptance criteria used for development and testing.",
            S["body"],
        )
    )
    story.append(Paragraph("1.1 Roles", S["h2"]))
    story.append(
        formal_table(
            ["Role", "Primary responsibilities"],
            [
                ["AGENT", "Room search, booking, view contracts and bookings"],
                ["MANAGER", "All AGENT capabilities plus create, edit and delete contracts"],
                ["ADMIN", "All capabilities plus user administration"],
            ],
            [35 * mm, 135 * mm],
            S,
        )
    )

    def add_story(code, title, as_a, want, so_that, criteria):
        story.append(Paragraph(f"{code} — {title}", S["h3"]))
        story.append(Paragraph(f"<b>As a</b> {as_a}", S["story_as"]))
        story.append(Paragraph(f"<b>I want</b> {want}", S["story_as"]))
        story.append(Paragraph(f"<b>So that</b> {so_that}", S["story_as"]))
        story.append(Paragraph("<b>Acceptance criteria</b>", S["body"]))
        story.append(bullets(criteria, S))
        story.append(Spacer(1, 4))

    story.append(Paragraph("2. Authentication and access", S["h1"]))
    add_story(
        "US-01",
        "Login",
        "Sun Travels staff member",
        "to sign in with username and password",
        "I only access features allowed for my role",
        [
            "Valid credentials return a session token and open the ERP workspace",
            "Invalid credentials display a clear error message",
            "Empty required fields prompt the user to complete them",
        ],
    )
    add_story(
        "US-02",
        "Agent self-registration",
        "new call-centre agent",
        "to create an account with a strong password",
        "I can search and book without waiting for an administrator",
        [
            "Registration creates an AGENT account only",
            "Weak passwords are rejected with a policy explanation",
            "Password and confirmation must match",
        ],
    )
    add_story(
        "US-03",
        "Role-based navigation",
        "ADMIN, MANAGER or AGENT",
        "the sidebar to show only modules I am allowed to use",
        "I am not presented with actions outside my authority",
        [
            "Navigation options reflect the authenticated role",
            "Unauthorised routes redirect the user appropriately",
        ],
    )

    story.append(Paragraph("3. Hotel contracts", S["h1"]))
    add_story(
        "US-04",
        "Load contract",
        "MANAGER",
        "to enter hotel name, validity period, markup and room types",
        "paper contracts become searchable inventory in the system",
        [
            "Markup defaults to 1.15 (15%) when not overridden",
            "At least one room type is required (name, price per person per night, number of rooms, max adults)",
            "Missing required fields show a clear validation message",
        ],
    )
    add_story(
        "US-05",
        "View and search contracts",
        "AGENT or MANAGER",
        "to list and search contracts by hotel name",
        "I can verify which contracts are loaded",
        [
            "Contracts are listed with validity, markup and room summary",
            "Search by hotel name filters the list",
        ],
    )
    add_story(
        "US-06",
        "Edit or delete contract",
        "MANAGER",
        "to update or remove a contract",
        "corrections to paper terms are reflected accurately",
        [
            "Existing contracts can be edited and saved",
            "Deletion requires confirmation before removal",
        ],
    )

    story.append(Paragraph("4. Room search", S["h1"]))
    add_story(
        "US-07",
        "Search rooms with pricing",
        "AGENT",
        "to enter check-in date, nights and rooms with adult counts",
        "I see matching hotels with customer prices and availability",
        [
            "Results show hotel name, room type, marked-up price and Available / Not available",
            "Price = hotelier rate × markup × nights × adults (summed for each requested room)",
            "Stay must fall fully inside the contract validity period",
            "Available only when free rooms cover the request and adults do not exceed max adults",
        ],
    )
    add_story(
        "US-08",
        "Search results usability",
        "AGENT",
        "to sort, filter and paginate search results",
        "large result sets remain easy to work with",
        [
            "Column headers support sorting",
            "Text and availability filters narrow the visible rows",
            "Pagination limits rows per page",
        ],
    )

    story.append(Paragraph("5. Bookings", S["h1"]))
    add_story(
        "US-09",
        "Book a room",
        "AGENT",
        "to select an available room number and enter guest details",
        "the stay is reserved for the customer",
        [
            "Required fields: room number, guest full name, NIC, adults",
            "Optional fields: phone, email",
            "The same room number cannot be confirmed twice for overlapping dates",
        ],
    )
    add_story(
        "US-10",
        "View and cancel bookings",
        "staff member",
        "to list bookings and cancel confirmed ones",
        "inventory is released when plans change",
        [
            "Bookings show guest, hotel, room type, room number, stay and status",
            "Confirmed bookings can be cancelled with confirmation",
        ],
    )

    story.append(Paragraph("6. Administration and profile", S["h1"]))
    add_story(
        "US-11",
        "Manage users",
        "ADMIN",
        "to create, edit and activate or deactivate users and assign roles",
        "access control remains accurate",
        [
            "Administrators can create and update users",
            "Roles ADMIN, MANAGER and AGENT can be assigned",
            "Strong password policy applies when setting passwords",
        ],
    )
    add_story(
        "US-12",
        "Update profile",
        "logged-in user",
        "to update my name or email and change my password",
        "my account details stay current",
        [
            "Profile details can be saved",
            "Password change requires current password and a strong new password",
        ],
    )

    story.append(Paragraph("7. Operational quality", S["h1"]))
    add_story(
        "US-13",
        "Responsive interface during processing",
        "user",
        "loading indicators while search or save operations run",
        "the interface remains responsive and clear",
        [
            "Long operations show progress or disabled submit states",
            "Success and error messages are displayed after completion",
        ],
    )
    add_story(
        "US-14",
        "Repeatable database setup",
        "developer setting up the project",
        "to start the database with schema using Docker Compose",
        "the environment can be prepared consistently on another machine",
        [
            "docker compose up -d starts MySQL with the project schema",
            "Application configuration matches the published database port and credentials",
        ],
    )

    story.append(Paragraph("8. Traceability summary", S["h1"]))
    story.append(
        formal_table(
            ["Area", "Stories"],
            [
                ["Authentication", "US-01, US-02, US-03"],
                ["Contracts", "US-04, US-05, US-06"],
                ["Search", "US-07, US-08"],
                ["Bookings", "US-09, US-10"],
                ["Admin / profile", "US-11, US-12"],
                ["Quality / setup", "US-13, US-14"],
            ],
            [45 * mm, 125 * mm],
            S,
        )
    )

    doc.build(story, onFirstPage=header_footer, onLaterPages=header_footer)
    return path


def build_user_manual():
    S = styles()
    path = OUT / "SunTravels-User-Manual.pdf"
    doc = SimpleDocTemplate(
        str(path),
        pagesize=A4,
        leftMargin=20 * mm,
        rightMargin=20 * mm,
        topMargin=22 * mm,
        bottomMargin=20 * mm,
        title="Sun Travels ERP — User Manual",
        author="Sun Travels Training Assignment",
    )
    story = []
    cover(story, S, "User Manual", "Guide to installing, signing in and using Sun Travels ERP")

    story.append(Paragraph("1. Overview", S["h1"]))
    story.append(
        Paragraph(
            "Sun Travels ERP supports call-centre and operations staff who load hotel contracts, "
            "search room availability with marked-up prices, and confirm guest bookings against "
            "specific room numbers.",
            S["body"],
        )
    )
    story.append(
        formal_table(
            ["Application", "Address"],
            [["User interface", "http://localhost:4200"], ["Backend API", "http://localhost:8080"]],
            [50 * mm, 120 * mm],
            S,
        )
    )

    story.append(Paragraph("2. Starting the system", S["h1"]))
    story.append(Paragraph("2.1 Database", S["h2"]))
    story.append(
        Paragraph(
            "From the project root, start MySQL with Docker Compose. The compose file publishes "
            "the database on host port <b>3307</b> and applies the project schema on first start.",
            S["body"],
        )
    )
    story.append(bullets(["Command: <font face='Courier'>docker compose up -d</font>",
                          "Database: suntravels",
                          "User / password: suntravels / suntravels",
                          "Host port: 3307"], S))
    story.append(Paragraph("2.2 Backend", S["h2"]))
    story.append(Paragraph("Open a terminal in the <b>backend</b> folder and run:", S["body"]))
    story.append(Paragraph("<font face='Courier'>./mvnw spring-boot:run</font>", S["body"]))
    story.append(Paragraph("2.3 Frontend", S["h2"]))
    story.append(Paragraph("Open a terminal in the <b>frontend</b> folder and run:", S["body"]))
    story.append(Paragraph("<font face='Courier'>npm install</font>", S["body"]))
    story.append(Paragraph("<font face='Courier'>npm start</font>", S["body"]))

    story.append(Paragraph("2.4 Demo accounts", S["h2"]))
    story.append(
        formal_table(
            ["Username", "Password", "Role"],
            [
                ["admin", "Admin@12345", "ADMIN"],
                ["manager", "Manager@12345", "MANAGER"],
                ["agent", "Agent@12345", "AGENT"],
            ],
            [45 * mm, 55 * mm, 40 * mm],
            S,
        )
    )

    story.append(Paragraph("3. Sign in", S["h1"]))
    story.append(
        Paragraph(
            "Open the user interface and sign in with a demo account or a registered agent account. "
            "Empty username or password fields display a message asking you to complete the required fields. "
            "New agents may create an account from the sign-up page; a strong password is required.",
            S["body"],
        )
    )

    story.append(Paragraph("4. Contracts", S["h1"]))
    story.append(
        Paragraph(
            "Managers and administrators use <b>Contracts</b> to load paper hotel agreements. "
            "Enter hotel name, validity dates, markup (for example 1.15 for 15%), and one or more room types. "
            "Each room type needs a name, price per person per night, number of rooms and maximum adults. "
            "Agents may view and search contracts but cannot edit them.",
            S["body"],
        )
    )
    story.append(shot(SHOTS / "contracts-list.png", "Figure 1 — Contracts list with hotel search", S))
    story.append(shot(SHOTS / "contract-room-types.png", "Figure 2 — Room types on the contract form", S))
    story.append(shot(SHOTS / "contract-row.png", "Figure 3 — Contract detail including markup and rooms", S, max_height=55 * mm))

    story.append(Paragraph("5. Room search", S["h1"]))
    story.append(
        Paragraph(
            "Open <b>Room search</b>. Enter check-in date, number of nights, and adults for each required room. "
            "Use <b>Add room</b> when the customer needs more than one room. Click <b>Find rooms</b>.",
            S["body"],
        )
    )
    story.append(
        Paragraph(
            "Results show hotel name, room type, total customer price and availability. "
            "Price is calculated as <b>hotelier rate × markup × nights × adults</b>, summed across the rooms in the search. "
            "A room type is Available when enough free rooms exist for the stay and adult counts fit the maximum adults. "
            "Use filters, column sorting and pagination to work through large result lists.",
            S["body"],
        )
    )
    story.append(shot(SHOTS / "search-results.png", "Figure 4 — Search results with price, availability and pagination", S))

    story.append(Paragraph("6. Booking a room", S["h1"]))
    story.append(bullets([
        "On an Available row, click <b>Book room</b>. The booking form opens below and the page scrolls to it.",
        "Select a free <b>room number</b> for the stay dates.",
        "Enter guest full name, NIC and adults. Phone and email are optional.",
        "Click <b>Confirm booking</b>.",
        "If the chosen room was taken by another booking for overlapping dates, an error is shown on the booking form.",
    ], S))

    story.append(Paragraph("7. Bookings", S["h1"]))
    story.append(
        Paragraph(
            "Open <b>Bookings</b> to review confirmed and cancelled reservations. "
            "Each row shows guest details, hotel and room type, room number when assigned, stay dates and status. "
            "Cancel a confirmed booking when the customer no longer needs the room; the room number becomes available again for those dates.",
            S["body"],
        )
    )
    story.append(shot(SHOTS / "bookings-list.png", "Figure 5 — Bookings list with guest, stay and status", S))

    story.append(Paragraph("8. Users (ADMIN)", S["h1"]))
    story.append(
        Paragraph(
            "Administrators open <b>Users</b> to create or edit accounts, assign roles (ADMIN, MANAGER, AGENT), "
            "set active status and manage passwords under the strong-password policy.",
            S["body"],
        )
    )

    story.append(Paragraph("9. Profile", S["h1"]))
    story.append(
        Paragraph(
            "Every signed-in user can update full name and email, and change password by providing the current "
            "password and a strong new password.",
            S["body"],
        )
    )

    story.append(Paragraph("10. Role permissions", S["h1"]))
    story.append(
        formal_table(
            ["Feature", "AGENT", "MANAGER", "ADMIN"],
            [
                ["Room search and booking", "Yes", "Yes", "Yes"],
                ["View contracts", "Yes", "Yes", "Yes"],
                ["Create / edit / delete contracts", "No", "Yes", "Yes"],
                ["Manage users", "No", "No", "Yes"],
            ],
            [70 * mm, 30 * mm, 30 * mm, 30 * mm],
            S,
        )
    )

    story.append(Paragraph("11. Troubleshooting", S["h1"]))
    story.append(
        formal_table(
            ["Situation", "Recommended action"],
            [
                ["Cannot sign in", "Confirm the API is running on port 8080 and use a demo account"],
                ["Port 8080 already in use", "Stop the other process using the port, then restart the backend"],
                ["No search results", "Ensure a contract covers the full stay dates"],
                ["Room already booked", "Choose another free room number from the dropdown"],
                ["Database connection error", "Run docker compose ps and confirm MySQL is healthy on port 3307"],
            ],
            [55 * mm, 115 * mm],
            S,
        )
    )

    doc.build(story, onFirstPage=header_footer, onLaterPages=header_footer)
    return path


def build_test_plan_and_cases():
    S = styles()
    path = OUT / "SunTravels-Test-Plan-and-Test-Cases.pdf"
    doc = SimpleDocTemplate(
        str(path),
        pagesize=A4,
        leftMargin=18 * mm,
        rightMargin=18 * mm,
        topMargin=22 * mm,
        bottomMargin=20 * mm,
        title="Sun Travels ERP — Test Plan and Test Cases",
        author="Sun Travels Training Assignment",
    )
    story = []
    cover(
        story,
        S,
        "Test Plan and Test Cases",
        "Test strategy, entry and exit criteria, and detailed functional test cases",
    )

    # ---- Test plan ----
    story.append(Paragraph("Part A — Test Plan", S["h1"]))
    story.append(Paragraph("1. Purpose", S["h2"]))
    story.append(
        Paragraph(
            "This test plan describes how Sun Travels ERP is verified against the training assignment "
            "requirements and the agreed user stories. It covers authentication and roles, contract management, "
            "room search pricing and availability, booking with room-number conflict control, bookings maintenance, "
            "administration, and user-interface validation behaviour.",
            S["body"],
        )
    )

    story.append(Paragraph("2. Scope", S["h2"]))
    story.append(Paragraph("The following areas are included in testing:", S["body"]))
    story.append(bullets([
        "Authentication (login, signup, session token)",
        "Contracts create, view, search, edit and delete with role checks",
        "Search criteria, marked-up price formula, Available / Not available",
        "Booking with room number selection and overlapping-date protection",
        "Bookings list and cancellation",
        "User administration and profile updates",
        "UI validation messages, loading states and empty states",
        "Automated unit tests for backend services and selected Angular UI components",
    ], S))

    story.append(Paragraph("3. Test items", S["h2"]))
    story.append(
        formal_table(
            ["ID", "Item"],
            [
                ["TI-01", "Backend SearchService — pricing and availability"],
                ["TI-02", "Backend BookingService — room lock and overlap control"],
                ["TI-03", "Backend AuthService — login and signup rules"],
                ["TI-04", "Angular authentication, guards and search flows"],
                ["TI-05", "Contracts user interface and API"],
                ["TI-06", "End-to-end agent path: search → book → view booking"],
            ],
            [25 * mm, 145 * mm],
            S,
        )
    )

    story.append(Paragraph("4. Approach", S["h2"]))
    story.append(bullets([
        "<b>Unit testing:</b> JUnit service tests; Jasmine/Karma Angular component and guard tests",
        "<b>Manual functional testing:</b> execute the test cases in Part B of this document",
        "<b>Peer testing:</b> execute cases against a colleague’s build and record findings",
    ], S))

    story.append(Paragraph("5. Entry criteria", S["h2"]))
    story.append(Paragraph("Testing of a build may begin only when all of the following are true:", S["body"]))
    story.append(bullets([
        "Backend starts successfully and connects to MySQL",
        "Frontend builds and serves without compile errors",
        "Demo users can sign in (admin, manager, agent)",
        "At least one contract covers the intended test stay dates, or seed data is present",
        "Environment URLs are known (UI http://localhost:4200, API http://localhost:8080)",
        "This test plan and the test cases are available to the tester",
    ], S))

    story.append(Paragraph("6. Exit criteria", S["h2"]))
    story.append(Paragraph("A build may be signed off when:", S["body"]))
    story.append(bullets([
        "All Critical and High test cases have passed",
        "No open Critical defects remain",
        "Medium defects are documented with workaround or fix plan",
        "Backend unit tests (Auth, Search, Booking) pass locally",
        "Frontend unit tests pass locally",
        "Peer testing has been completed and findings recorded",
        "User manual matches the demonstrated user interface labels for the main flows",
    ], S))

    story.append(Paragraph("7. Pass and fail rules", S["h2"]))
    story.append(bullets([
        "<b>Pass:</b> actual result matches the expected result in the test case",
        "<b>Fail:</b> any deviation; record defect identifier, steps, severity and evidence",
    ], S))
    story.append(
        formal_table(
            ["Severity", "Meaning"],
            [
                ["Critical", "Cannot login, search or book; incorrect price formula; same room number double-booked"],
                ["High", "Role restriction broken; contract save incorrect; availability clearly wrong"],
                ["Medium", "Validation or usability issues; pagination or sorting defects"],
                ["Low", "Cosmetic presentation issues"],
            ],
            [35 * mm, 135 * mm],
            S,
        )
    )

    story.append(Paragraph("8. Responsibilities", S["h2"]))
    story.append(
        formal_table(
            ["Role", "Responsibility"],
            [
                ["Developer", "Maintain unit tests and resolve defects"],
                ["Peer tester", "Execute cases on the peer system and raise findings"],
                ["Author", "Regression before demonstration and viva"],
            ],
            [40 * mm, 130 * mm],
            S,
        )
    )

    story.append(Paragraph("9. Environment", S["h2"]))
    story.append(bullets([
        "Operating system: Windows, macOS or Linux",
        "JDK 21 or newer, Node.js 18 or newer, Docker with MySQL 8",
        "Browsers: current Chrome or Microsoft Edge",
    ], S))

    story.append(Paragraph("10. Risks and mitigations", S["h2"]))
    story.append(
        formal_table(
            ["Risk", "Mitigation"],
            [
                ["Port 8080 or 3307 already in use", "Stop conflicting processes or adjust published ports and configuration together"],
                ["Seed data already loaded", "Seeders are idempotent; recreate the Docker volume for a clean database if required"],
                ["Test dates outside contract validity", "Use dates inside an active contract period"],
            ],
            [55 * mm, 115 * mm],
            S,
        )
    )

    story.append(PageBreak())

    # ---- Test cases ----
    story.append(Paragraph("Part B — Test Cases", S["h1"]))
    story.append(
        Paragraph(
            "Priority key: <b>C</b> = Critical, <b>H</b> = High, <b>M</b> = Medium. "
            "Execute cases after entry criteria are satisfied.",
            S["body"],
        )
    )

    def cases_table(title, rows):
        story.append(Paragraph(title, S["h2"]))
        story.append(
            formal_table(
                ["ID", "Title", "Steps", "Expected result", "P"],
                rows,
                [18 * mm, 32 * mm, 52 * mm, 52 * mm, 10 * mm],
                S,
            )
        )
        story.append(Spacer(1, 6))

    cases_table(
        "B.1 Authentication",
        [
            ["TC-A01", "Login success", "Sign in as agent / Agent@12345", "ERP workspace opens; search is available", "C"],
            ["TC-A02", "Wrong password", "Valid username with incorrect password", "Error shown; remain on sign-in page", "C"],
            ["TC-A03", "Empty fields", "Submit sign-in with blank fields", "Message to complete username and password", "M"],
            ["TC-A04", "Signup strong password", "Register with a strong password", "AGENT account created; can sign in", "H"],
            ["TC-A05", "Signup weak password", "Register with password 123", "Rejected with password policy message", "H"],
        ],
    )
    cases_table(
        "B.2 Contracts",
        [
            ["TC-C01", "Agent cannot edit", "Sign in as agent; open Contracts", "No create/edit/delete controls", "H"],
            ["TC-C02", "Create contract", "As manager: hotel, dates, markup 1.15, Standard $35, 15 rooms, max 2", "Contract saved and listed", "C"],
            ["TC-C03", "Missing fields", "Save contract with empty hotel name", "Required-field message shown", "M"],
            ["TC-C04", "Search contracts", "Enter hotel name and search", "List filtered by hotel name", "M"],
            ["TC-C05", "Edit markup", "Change markup 1.15 to 1.20 and save", "Later search prices use the new markup", "H"],
        ],
    )
    cases_table(
        "B.3 Room search",
        [
            ["TC-S01", "Successful search", "Check-in inside validity; 2 nights; 1 room with 2 adults", "Results show hotel, type, price, status", "C"],
            ["TC-S02", "Price formula", "Rate 35, markup 1.15, 2 nights, 2 adults", "Price equals 161.00", "C"],
            ["TC-S03", "Outside validity", "Check-in outside contract period", "Contract hotel not returned for that stay", "C"],
            ["TC-S04", "Adults exceed max", "Standard max 2; request 3 adults", "Standard marked Not available", "C"],
            ["TC-S05", "Rooms exceed stock", "Request more rooms than free inventory", "Marked Not available", "C"],
            ["TC-S06", "Multi-room search", "1 room × 2 adults and 1 room × 1 adult", "Price sums both adult lines", "H"],
            ["TC-S07", "Empty criteria", "Search without check-in date", "Message to complete check-in date", "M"],
            ["TC-S08", "Pagination", "Search against a large result set", "About 10 rows per page; Next advances", "M"],
        ],
    )
    cases_table(
        "B.4 Booking",
        [
            ["TC-B01", "Book available room", "Book room → choose free number → guest and NIC → confirm", "Booking created with room number", "C"],
            ["TC-B02", "Double book same room", "Two users book same type, dates and room number", "Second booking rejected with form error", "C"],
            ["TC-B03", "Different room numbers", "Same type and dates; Room 1 then Room 2", "Both succeed when stock allows", "H"],
            ["TC-B04", "Cancel frees room", "Cancel a confirmed booking; book same room again", "Re-booking is allowed", "H"],
            ["TC-B05", "Missing guest fields", "Confirm without name or NIC", "Required-field message on booking form", "M"],
        ],
    )
    cases_table(
        "B.5 Administration and profile",
        [
            ["TC-U01", "Admin creates user", "Users → create MANAGER with strong password", "New user can sign in", "H"],
            ["TC-U02", "Agent blocked from Users", "As agent, open Users route", "Redirected or access denied", "H"],
            ["TC-P01", "Change password", "Profile → current password + strong new password", "Success; sign in with new password", "M"],
        ],
    )

    story.append(Paragraph("B.6 Automated unit tests", S["h2"]))
    story.append(Paragraph("Backend:", S["body"]))
    story.append(Paragraph("<font face='Courier'>cd backend &amp;&amp; ./mvnw test</font>", S["body"]))
    story.append(Paragraph("Frontend:", S["body"]))
    story.append(Paragraph("<font face='Courier'>cd frontend &amp;&amp; npx ng test --no-watch --browsers=ChromeHeadless</font>", S["body"]))
    story.append(
        formal_table(
            ["Suite", "Coverage"],
            [
                ["SearchServiceTest", "Marked-up price, availability rules, empty covering contracts"],
                ["BookingServiceTest", "Overlapping same room number rejected; free rooms listed"],
                ["AuthServiceTest", "Login success and failures; signup AGENT; weak password; duplicate username"],
                ["Angular specs", "Guards, login/signup, search validation, filter and pagination helpers"],
            ],
            [45 * mm, 125 * mm],
            S,
        )
    )

    story.append(Paragraph("B.7 Requirements traceability", S["h2"]))
    story.append(
        formal_table(
            ["User story", "Related test cases"],
            [
                ["US-01 Login", "TC-A01, TC-A02, TC-A03"],
                ["US-02 Signup", "TC-A04, TC-A05"],
                ["US-04 Load contract", "TC-C02, TC-C03"],
                ["US-07 Room search", "TC-S01 to TC-S07"],
                ["US-09 Book a room", "TC-B01 to TC-B05"],
                ["US-11 Manage users", "TC-U01, TC-U02"],
            ],
            [55 * mm, 115 * mm],
            S,
        )
    )

    doc.build(story, onFirstPage=header_footer, onLaterPages=header_footer)
    return path


if __name__ == "__main__":
    paths = [
        build_user_stories(),
        build_user_manual(),
        build_test_plan_and_cases(),
    ]
    print("Generated:")
    for p in paths:
        print(f" - {p}")
