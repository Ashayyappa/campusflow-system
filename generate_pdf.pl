#!/usr/bin/env perl
use strict;
use warnings;

# Pure Perl PDF Generator (Zero External Modules Required)
# Generates a valid multi-page PDF 1.4 document for CampusFlow Project Report.

my $pdf_file = "PROJECT_REPORT.pdf";
open(my $fh, '>:raw', $pdf_file) or die "Cannot open $pdf_file: $!";

my @objects;
my @pages;
my $obj_count = 0;

sub add_object {
    my ($content) = @_;
    $obj_count++;
    push @objects, $content;
    return $obj_count;
}

# Object 1: Catalog (placeholder, updated at end)
# Object 2: Pages (placeholder)
# Object 3: Font Helvetica
# Object 4: Font Helvetica-Bold

# We will collect pages and their streams
my @page_streams;

sub escape_pdf_str {
    my ($str) = @_;
    $str =~ s/\\/\\\\/g;
    $str =~ s/\(/\\(/g;
    $str =~ s/\)/\\)/g;
    return $str;
}

# Read markdown report lines
open(my $rfh, '<', 'PROJECT_REPORT.md') or die "Cannot read PROJECT_REPORT.md: $!";
my @lines = <$rfh>;
close($rfh);

my @current_stream;
my $y = 770;
my $page_num = 1;

sub start_new_page {
    if (@current_stream) {
        push @page_streams, [ @current_stream ];
        @current_stream = ();
    }
    $y = 770;
}

# Header/Footer helper
sub add_header_footer {
    my ($pg) = @_;
    push @current_stream, "0.5 0.5 0.5 RG 0.5 w 40 805 m 555 805 l S";
    push @current_stream, "BT /F1 8 Tf 40 810 Td (CampusFlow: Smart Campus Resource Allocation Engine | Project Report) Tj ET";
    push @current_stream, "0.5 0.5 0.5 RG 0.5 w 40 45 m 555 45 l S";
    push @current_stream, "BT /F1 8 Tf 40 32 Td (VITyarthi Flipped Course Evaluation) Tj ET";
    push @current_stream, sprintf("BT /F1 8 Tf 520 32 Td (Page %d) Tj ET", $pg);
}

start_new_page();
add_header_footer($page_num);

foreach my $line (@lines) {
    chomp($line);
    $line =~ s/\r$//;

    if ($line =~ /^#\s+(.+)$/) {
        my $txt = $1;
        if ($y < 700) {
            $page_num++;
            start_new_page();
            add_header_footer($page_num);
        }
        $y -= 25;
        push @current_stream, sprintf("BT /F2 18 Tf 0.1 0.25 0.5 rg 40 %d Td (%s) Tj ET", $y, escape_pdf_str($txt));
        $y -= 15;
    } elsif ($line =~ /^##\s+(.+)$/) {
        my $txt = $1;
        $y -= 18;
        if ($y < 80) {
            $page_num++;
            start_new_page();
            add_header_footer($page_num);
        }
        push @current_stream, sprintf("BT /F2 13 Tf 0.15 0.35 0.6 rg 40 %d Td (%s) Tj ET", $y, escape_pdf_str($txt));
        $y -= 12;
    } elsif ($line =~ /^###\s+(.+)$/) {
        my $txt = $1;
        $y -= 16;
        if ($y < 80) {
            $page_num++;
            start_new_page();
            add_header_footer($page_num);
        }
        push @current_stream, sprintf("BT /F2 11 Tf 0.2 0.2 0.2 rg 40 %d Td (%s) Tj ET", $y, escape_pdf_str($txt));
        $y -= 10;
    } elsif ($line =~ /^####\s+(.+)$/) {
        my $txt = $1;
        $y -= 14;
        if ($y < 80) {
            $page_num++;
            start_new_page();
            add_header_footer($page_num);
        }
        push @current_stream, sprintf("BT /F2 10 Tf 0.25 0.25 0.25 rg 40 %d Td (%s) Tj ET", $y, escape_pdf_str($txt));
        $y -= 8;
    } elsif ($line =~ /^---\s*$/) {
        $y -= 8;
        push @current_stream, sprintf("0.8 0.8 0.8 RG 0.5 w 40 %d m 555 %d l S", $y, $y);
        $y -= 12;
    } elsif ($line eq '') {
        $y -= 8;
    } else {
        # Regular paragraph or bullet
        my $is_bullet = ($line =~ /^\s*[-*]\s+/);
        my $clean = $line;
        $clean =~ s/^\s*[-*]\s+/- /;
        $clean =~ s/\x{2014}/--/g;
        $clean =~ s/\x{2019}/'/g;
        $clean =~ s/\x{2018}/'/g;
        $clean =~ s/\x{201C}/"/g;
        $clean =~ s/\x{201D}/"/g;
        $clean =~ s/\*\*(.*?)\*\*/$1/g;
        $clean =~ s/\*(.*?)\*/$1/g;
        $clean =~ s/`([^`]+)`/$1/g;

        # Basic word wrapping at ~85 chars
        my @words = split(/\s+/, $clean);
        my $curr = "";
        foreach my $w (@words) {
            if (length($curr) + length($w) + 1 > 82) {
                if ($y < 65) {
                    $page_num++;
                    start_new_page();
                    add_header_footer($page_num);
                }
                push @current_stream, sprintf("BT /F1 9 Tf 0.1 0.1 0.1 rg %d %d Td (%s) Tj ET",
                    ($is_bullet ? 50 : 40), $y, escape_pdf_str($curr));
                $y -= 12;
                $curr = $w;
            } else {
                $curr = ($curr eq "") ? $w : "$curr $w";
            }
        }
        if ($curr ne "") {
            if ($y < 65) {
                $page_num++;
                start_new_page();
                add_header_footer($page_num);
            }
            push @current_stream, sprintf("BT /F1 9 Tf 0.1 0.1 0.1 rg %d %d Td (%s) Tj ET",
                ($is_bullet ? 50 : 40), $y, escape_pdf_str($curr));
            $y -= 12;
        }
    }
}

if (@current_stream) {
    push @page_streams, [ @current_stream ];
}

my $total_pages = scalar(@page_streams);

# Generate PDF structure
my @xref;
push @xref, 0; # 0-th object

my $output = "%PDF-1.4\n%\xe2\xe3\xcf\xd3\n";

# Obj 1: Catalog
my $catalog_pos = length($output);
push @xref, $catalog_pos;
$output .= "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n";

# Obj 2: Pages container
my $pages_pos = length($output);
push @xref, $pages_pos;
# Page objects will be numbered 5 .. (5 + $total_pages - 1)
my @kids;
for my $i (1 .. $total_pages) {
    push @kids, (4 + $i * 2 - 1) . " 0 R";
}
my $kids_str = join(" ", @kids);
$output .= "2 0 obj\n<< /Type /Pages /Kids [$kids_str] /Count $total_pages >>\nendobj\n";

# Obj 3: Font F1 (Helvetica)
my $f1_pos = length($output);
push @xref, $f1_pos;
$output .= "3 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>\nendobj\n";

# Obj 4: Font F2 (Helvetica-Bold)
my $f2_pos = length($output);
push @xref, $f2_pos;
$output .= "4 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold /Encoding /WinAnsiEncoding >>\nendobj\n";

my $next_obj = 5;
for my $idx (0 .. $total_pages - 1) {
    my $page_obj_num = $next_obj++;
    my $stream_obj_num = $next_obj++;

    my $stream_content = join("\n", @{ $page_streams[$idx] });
    my $stream_len = length($stream_content);

    # Page Object
    push @xref, length($output);
    $output .= "$page_obj_num 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 3 0 R /F2 4 0 R >> >> /Contents $stream_obj_num 0 R >>\nendobj\n";

    # Stream Object
    push @xref, length($output);
    $output .= "$stream_obj_num 0 obj\n<< /Length $stream_len >>\nstream\n$stream_content\nendstream\nendobj\n";
}

my $xref_pos = length($output);
$output .= "xref\n0 " . ($#xref + 1) . "\n";
$output .= "0000000000 65535 f \n";
for my $i (1 .. $#xref) {
    $output .= sprintf("%010d 00000 n \n", $xref[$i]);
}
$output .= "trailer\n<< /Size " . ($#xref + 1) . " /Root 1 0 R >>\nstartxref\n$xref_pos\n%%EOF\n";

print $fh $output;
close($fh);

print "Successfully generated $pdf_file with $total_pages pages (" . length($output) . " bytes).\n";
